package com.example.budgetingapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgetingapp.databinding.FragmentTransactionsBinding;
import com.example.budgetingapp.ui.AddEditTransactionActivity;

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
        return binding.getRoot();
    }

    private void setAddButtonListener() {
        binding.buttonAddTransaction.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AddEditTransactionActivity.class);
            intent.putExtra(AddEditTransactionActivity.IN_EXTRA_ACTIVITY_TYPE,
                    AddEditTransactionActivity.IN_EXTRA_ACTIVITY_TYPE_ADD);
            startActivity(intent);
//            addEditTransactionActivityResultLauncher.launch(intent);
        });
    }
}