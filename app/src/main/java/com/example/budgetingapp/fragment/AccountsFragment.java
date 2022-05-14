package com.example.budgetingapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.budgetingapp.databinding.FragmentAccountsBinding;
import com.example.budgetingapp.adapter.AccountAdapter;
import com.example.budgetingapp.viewmodel.AccountVM;

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

        AccountAdapter accountAdapter = new AccountAdapter(getActivity());
        AccountVM accountVM = new ViewModelProvider(getActivity()).get(AccountVM.class);
        accountVM.getAllAccounts().observe(getActivity(), accountAdapter::setAccounts);
        initAccountRecyclerView(accountAdapter);

        return binding.getRoot();
    }

    private void initAccountRecyclerView(AccountAdapter accountAdapter) {
        binding.accountRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.accountRecyclerView.setHasFixedSize(true);
        binding.accountRecyclerView.setAdapter(accountAdapter);
    }
}