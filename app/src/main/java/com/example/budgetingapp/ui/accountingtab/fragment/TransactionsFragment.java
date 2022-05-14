package com.example.budgetingapp.ui.accountingtab.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.BudgetingAppDatabase;
import com.example.budgetingapp.databinding.FragmentTransactionsBinding;
import com.example.budgetingapp.entity.Account;
import com.example.budgetingapp.entity.Transaction;
import com.example.budgetingapp.entity.enums.TransactionType;
import com.example.budgetingapp.ui.accountingtab.AddEditTransactionActivity;
import com.example.budgetingapp.ui.accountingtab.adapter.TransactionAdapter;
import com.example.budgetingapp.viewmodel.AccountVM;
import com.example.budgetingapp.viewmodel.TransactionVM;

public class TransactionsFragment extends Fragment {
    private FragmentTransactionsBinding binding;

    public TransactionsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        binding = FragmentTransactionsBinding.inflate(inflater, container, false);

        TransactionVM txVM = getTransactionVM();
        TransactionOnClickCallback txOnClickCallback = this::startEditTransactionActivity;
        TransactionAdapter txAdapter = new TransactionAdapter(txOnClickCallback, getActivity(),
                binding.textViewNoTransactions);
        setDeleteTransactionOnSwipe(txAdapter);
        txVM.getAllTransactions().observe(getActivity(), txAdapter::setTransactions);
        initTransactionRecyclerView(txAdapter);

        setAddButtonListener();

        return binding.getRoot();
    }

    private TransactionVM getTransactionVM() {
        return new ViewModelProvider(getActivity()).get(TransactionVM.class);
    }

    private void startEditTransactionActivity(Transaction transaction) {
        Intent intent = new Intent(getActivity(), AddEditTransactionActivity.class);
        int activityTypeExtra = getActivityTypeExtra(transaction.type);
        intent.putExtra(AddEditTransactionActivity.EXTRA_ACTIVITY_TYPE, activityTypeExtra);
        intent.putExtra(AddEditTransactionActivity.EXTRA_EDITED_TRANSACTION_ID, transaction.id);
        startActivity(intent);
    }

    private int getActivityTypeExtra(TransactionType transactionType) {
        int activityTypeExtra;
        if (transactionType == TransactionType.EXPENSE) {
            activityTypeExtra = AddEditTransactionActivity.EXTRA_ACTIVITY_TYPE_EDIT_EXPENSE;
        } else {
            activityTypeExtra = AddEditTransactionActivity.EXTRA_ACTIVITY_TYPE_EDIT_INCOME;
        }
        return activityTypeExtra;
    }

    private void initTransactionRecyclerView(TransactionAdapter txAdapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        binding.txRecyclerView.setLayoutManager(layoutManager);
        binding.txRecyclerView.setHasFixedSize(true);
        binding.txRecyclerView.setAdapter(txAdapter);
    }

    private void setAddButtonListener() {
        binding.buttonAddTransaction.setOnClickListener(view -> {
            startAddTransactionActivity();
        });
    }

    private void startAddTransactionActivity() {
        Intent intent = new Intent(getActivity(), AddEditTransactionActivity.class);
        intent.putExtra(AddEditTransactionActivity.EXTRA_ACTIVITY_TYPE,
                AddEditTransactionActivity.EXTRA_ACTIVITY_TYPE_ADD);
        startActivity(intent);
    }

    private void setDeleteTransactionOnSwipe(TransactionAdapter txAdapter) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                new AlertDialog.Builder(viewHolder.itemView.getContext())
                        .setMessage("DELETE TRANSACTION")
                        .setPositiveButton("OK", (dialogInterface, id) -> {
                            int position = viewHolder.getAdapterPosition();
                            Transaction tx = txAdapter.getTransactionAtPosition(position);
                            deleteTransactionAndUpdateBalances(tx);
                        })
                        .setNegativeButton("Cancel", (dialogInterface, id) -> {
                            txAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                        })
                        .create().show();
            }
        }).attachToRecyclerView(binding.txRecyclerView);
    }

    private AccountVM getAccountVM() {
        return new ViewModelProvider(getActivity()).get(AccountVM.class);
    }

    private void deleteTransactionAndUpdateBalances(Transaction tx) {
        TransactionVM txVM = getTransactionVM();
        AccountVM accountVM = getAccountVM();
        BudgetingAppDatabase db =
                BudgetingAppDatabase.getInstance(getActivity());
        db.runInTransaction(() -> {
            txVM.delete(tx);
            Account account = accountVM.getAccountByName(tx.accountName);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            long netWorth = sharedPref.getLong("NetWorth", 0L);

            if (tx.type == TransactionType.EXPENSE) {
                account.balance += tx.amount;
                netWorth += tx.amount;
            } else {
                account.balance -= tx.amount;
                netWorth -= tx.amount;
            }
            accountVM.update(account);
            sharedPref.edit()
                    .putLong("NetWorth", netWorth)
                    .apply();
        });
    }

    public interface TransactionOnClickCallback {
        void onClick(Transaction transaction);
    }
}