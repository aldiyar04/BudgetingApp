package com.example.budgetingapp.ui.accountingtab.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.budgetingapp.databinding.FragmentTransactionsBinding;
import com.example.budgetingapp.entity.Transaction;
import com.example.budgetingapp.entity.enums.TransactionType;
import com.example.budgetingapp.ui.accountingtab.AddEditTransactionActivity;
import com.example.budgetingapp.ui.accountingtab.adapter.TransactionAdapter;
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

        TransactionVM txVM = new ViewModelProvider(getActivity()).get(TransactionVM.class);
        TransactionAdapter txAdapter = new TransactionAdapter(this::startEditTransactionActivity);
        txVM.getAllTransactions().observe(getActivity(), txAdapter::setTransactions);
        initTransactionRecyclerView(txAdapter);

        setAddButtonListener();

        return binding.getRoot();
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

    public interface TransactionOnClickCallback {
        void onClick(Transaction transaction);
    }
}