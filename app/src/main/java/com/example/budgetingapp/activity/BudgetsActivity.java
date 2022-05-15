package com.example.budgetingapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.ActivityBudgetsBinding;

public class BudgetsActivity extends AppCompatActivity {
    private ActivityBudgetsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBudgetsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initBottomNav();


//        if (budgets.size() > 0) {
//            binding.textViewNoBudgets.setVisibility(View.GONE);
//            binding.buttonCreateFirstBudget.setVisibility(View.GONE);
//        } else {
//            binding.textViewNoBudgets.setVisibility(View.VISIBLE);
//            binding.buttonCreateFirstBudget.setVisibility(View.VISIBLE);
//        }
    }

    private void initBottomNav() {
        binding.bottomNav.setSelectedItemId(R.id.budgets);
        setBottomNavListeners();
    }

    private void setBottomNavListeners() {
        binding.bottomNav.setOnItemSelectedListener((item) -> {
            if (item.getItemId() == R.id.accounting) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.budgets) {
                return true;
            } else if (item.getItemId() == R.id.reports) {
                startActivity(new Intent(getApplicationContext(), ReportsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });
    }
}