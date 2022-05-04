package com.example.budgetingapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.ActivityMainBinding;
import com.example.budgetingapp.ui.fragment.AccountsFragment;
import com.example.budgetingapp.ui.fragment.TransactionsFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initBottomNav();
        if (savedInstanceState == null) {
            setTransactionsFragment();
        }
        initFragmentButtonListeners();
    }

    private void initBottomNav() {
        binding.bottomNav.setSelectedItemId(R.id.accounting);
        setBottomNavListeners();
    }

    private void setBottomNavListeners() {
        binding.bottomNav.setOnItemSelectedListener((item) -> {
            if (item.getItemId() == R.id.accounting) {
                return true;
            } else if (item.getItemId() == R.id.budgets) {
                startActivity(new Intent(getApplicationContext(), BudgetsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.reports) {
                startActivity(new Intent(getApplicationContext(), ReportsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });
    }

    private void setTransactionsFragment() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragmentContainer, TransactionsFragment.class, null)
                .commit();
    }

    private void initFragmentButtonListeners() {
        binding.btnTransactions.setOnClickListener(view -> {
            replaceFragment(TransactionsFragment.class);
        });
        binding.btnAccounts.setOnClickListener(view -> {
            replaceFragment(AccountsFragment.class);
        });
    }

    private void replaceFragment(Class<? extends Fragment> newFragmentClass) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragmentContainer, newFragmentClass, null)
                .commit();
    }
}