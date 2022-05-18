package com.example.budgetingapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.BudgetingAppDatabase;
import com.example.budgetingapp.R;
import com.example.budgetingapp.activity.AddEditTransactionActivity;
import com.example.budgetingapp.adapter.TransactionAdapter;
import com.example.budgetingapp.databinding.FragmentTransactionsBinding;
import com.example.budgetingapp.dialog.ConfirmDialog;
import com.example.budgetingapp.entity.Account;
import com.example.budgetingapp.entity.Transaction;
import com.example.budgetingapp.entity.enums.TransactionType;
import com.example.budgetingapp.viewmodel.AccountVM;
import com.example.budgetingapp.viewmodel.TransactionVM;

public class TransactionsFragment extends Fragment {
    private FragmentTransactionsBinding binding;
    private ActivityResultLauncher<Intent> addEditTransactionResultLauncher;
    private Parcelable topPositionRecyclerViewState;
    private Parcelable lastPositionRecyclerViewState;

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
        TransactionAdapter txAdapter = new TransactionAdapter(getActivity(),
                binding.textViewNoTransactions);
        txAdapter.setTransactionOnClickCallback(this::startEditTransactionActivity);
        txAdapter.setTransactionOnLongClickCallback(this::startDeleteTransactionConfirmDialog);
        txVM.getAllTransactions().observe(getActivity(), txAdapter::setTransactions);
        initTransactionRecyclerView(txAdapter);
        initAddEditTransactionResultLauncher();

        setAddButtonListener();

        return binding.getRoot();
    }

    private void initAddEditTransactionResultLauncher() {
        addEditTransactionResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    SettableScrollLinearLayoutManager layoutManager =
                            (SettableScrollLinearLayoutManager)
                                    binding.txRecyclerView.getLayoutManager();

                    Parcelable positionState = topPositionRecyclerViewState;

                    if (result.getResultCode() == AddEditTransactionActivity.RESULT_ADDED) {
                        layoutManager.setScrollToTop(true);
                        positionState = topPositionRecyclerViewState;
                    } else {
                        layoutManager.setScrollToTop(false);
                        if (result.getResultCode() == AddEditTransactionActivity.RESULT_EDITED) {
                            positionState = lastPositionRecyclerViewState;
                        }
                    }
                    binding.txRecyclerView.getLayoutManager()
                            .onRestoreInstanceState(positionState);
                });

    }

    private TransactionVM getTransactionVM() {
        return new ViewModelProvider(getActivity()).get(TransactionVM.class);
    }

    private boolean startEditTransactionActivity(View view, Transaction transaction) {
        // Save current recycler view position to restore after transaction is edited
        lastPositionRecyclerViewState =
                binding.txRecyclerView.getLayoutManager().onSaveInstanceState();

        Intent intent = new Intent(getActivity(), AddEditTransactionActivity.class);
        int activityTypeExtra = getActivityTypeExtra(transaction.type);
        intent.putExtra(AddEditTransactionActivity.EXTRA_ACTIVITY_TYPE, activityTypeExtra);
        intent.putExtra(AddEditTransactionActivity.EXTRA_EDITED_TRANSACTION_ID, transaction.id);
        addEditTransactionResultLauncher.launch(intent);

        return true;
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

    private boolean startDeleteTransactionConfirmDialog(View txView, Transaction tx) {
        ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setConfirmTitle("Delete transaction?");
        dialog.setOkButtonOnClickListener(view ->
                deleteTransactionAndUpdateBalances(tx)
        );
        dialog.setOnShowListener(dialogInterface -> {
            View txItemView = txView.findViewById(R.id.cardViewTransactionItem);
            txItemView.setBackgroundColor(getColor(R.color.yellow));
        });
        dialog.setOnDismissListener(dialogInterface -> {
            View txItemView = txView.findViewById(R.id.cardViewTransactionItem);
            txItemView.setBackgroundColor(getColor(R.color.white));
        });
        dialog.show();
        return true;
    }

    private int getColor(int colorID) {
        return ContextCompat.getColor(getActivity(), colorID);
    }

    private void initTransactionRecyclerView(TransactionAdapter txAdapter) {
        SettableScrollLinearLayoutManager layoutManager = new SettableScrollLinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, true);
        layoutManager.setScrollToTop(true);
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
        addEditTransactionResultLauncher.launch(intent);

        topPositionRecyclerViewState = binding.txRecyclerView.getLayoutManager()
                .onSaveInstanceState();
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
            Account account = accountVM.getAccountById(tx.accountId);
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

    private class SettableScrollLinearLayoutManager extends LinearLayoutManager {
        private boolean scrollToTop = false;
//        private int timesCalled = 0;

        public SettableScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public void setScrollToTop(boolean scrollToTop) {
            this.scrollToTop = scrollToTop;
        }

        @Override
        public void onLayoutCompleted(RecyclerView.State state) {
            super.onLayoutCompleted(state);
            if (scrollToTop && state.getItemCount() > 0) {
                int topPosition = state.getItemCount() - 1;
                scrollToPositionWithOffset(topPosition, 0);

                // For some reason scrollToPositionWithOffset() works only second time;
                // this causes flickering because txRecyclerView is shown initially at the wrong
                // position and then only on the second call it scrolls to the top.
//                timesCalled++;
//                if (timesCalled == 1) {
//                    binding.txRecyclerView.setVisibility(View.INVISIBLE);
//                } else if (timesCalled == 2) {
//                    binding.txRecyclerView.setVisibility(View.VISIBLE);
//                }
            }
        }

    }

    public interface TransactionViewCallback {
        boolean handle(View view, Transaction transaction);
    }
}