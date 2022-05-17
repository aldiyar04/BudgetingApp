package com.example.budgetingapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.ActivityMainBinding;
import com.example.budgetingapp.helper.LongSharedPrefLiveData;
import com.example.budgetingapp.helper.KztAmountFormatter;
import com.example.budgetingapp.fragment.AccountsFragment;
import com.example.budgetingapp.fragment.TransactionsFragment;
import com.google.android.material.button.MaterialButton;

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
            setFragmentButtonColors(binding.btnTransactions, binding.btnAccounts);
        }
        initFragmentButtonListeners();
        initObservingNetWorthPrefByHeaderView();
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
            setFragmentButtonColors(binding.btnTransactions, binding.btnAccounts);
        });
        binding.btnAccounts.setOnClickListener(view -> {
            replaceFragment(AccountsFragment.class);
            setFragmentButtonColors(binding.btnAccounts, binding.btnTransactions);
        });
    }

    private void setFragmentButtonColors(MaterialButton activeButton,
                                         MaterialButton nonActiveButton) {
        activeButton.setBackgroundColor(Color.rgb(235, 83, 83));
        activeButton.setTextColor(Color.WHITE);
        nonActiveButton.setBackgroundColor(Color.rgb(221, 221, 221));
        nonActiveButton.setTextColor(Color.BLACK);
    }

    private void replaceFragment(Class<? extends Fragment> newFragmentClass) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, newFragmentClass, null)
                .commit();
    }

    public void initObservingNetWorthPrefByHeaderView() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        LongSharedPrefLiveData longSharedPrefLiveData = new LongSharedPrefLiveData(sharedPref,
                "NetWorth", 0L);
        longSharedPrefLiveData.observe(this, this::setNetWorthInHeaderView);
    }

    private void setNetWorthInHeaderView(long netWorth) {
        String netWorthFormatted = KztAmountFormatter.format(netWorth);
        binding.textViewNetWorth.setText("Net Worth: " + netWorthFormatted);
    }
}