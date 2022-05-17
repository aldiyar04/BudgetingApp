package com.example.budgetingapp.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.FragmentExpensesByCategoryBinding;
import com.example.budgetingapp.databinding.FragmentTransactionsBinding;

public class ExpensesByCategoryFragment extends Fragment {
    private FragmentExpensesByCategoryBinding binding;

    public ExpensesByCategoryFragment() {}

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
        binding = FragmentExpensesByCategoryBinding.inflate(inflater, container, false);



        return binding.getRoot();
    }
}