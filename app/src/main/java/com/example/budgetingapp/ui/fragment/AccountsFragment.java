package com.example.budgetingapp.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.ActivityMainBinding;
import com.example.budgetingapp.databinding.FragmentAccountsBinding;

public class AccountsFragment extends Fragment {
    private FragmentAccountsBinding binding;

    public AccountsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAccountsBinding.inflate(getLayoutInflater());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.fragment_accounts, container, false);
    }
}