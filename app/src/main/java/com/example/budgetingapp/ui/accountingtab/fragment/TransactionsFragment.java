package com.example.budgetingapp.ui.accountingtab.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.databinding.FragmentTransactionsBinding;
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
        setAddButtonListener();

        TransactionAdapter txAdapter = new TransactionAdapter();
        TransactionVM txVM = new ViewModelProvider(getActivity()).get(TransactionVM.class);
        txVM.getAllTransactions().observe(getActivity(), txAdapter::setTransactions);
        RecyclerView txRecyclerView = binding.txRecyclerView;
        txRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        txRecyclerView.setHasFixedSize(true);
        txRecyclerView.setAdapter(txAdapter);

        return binding.getRoot();
    }

    private void setAddButtonListener() {
        binding.buttonAddTransaction.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AddEditTransactionActivity.class);
            intent.putExtra(AddEditTransactionActivity.EXTRA_ACTIVITY_TYPE,
                    AddEditTransactionActivity.EXTRA_ACTIVITY_TYPE_ADD);
            startActivity(intent);
//            addEditTransactionActivityResultLauncher.launch(intent);
        });
    }
}