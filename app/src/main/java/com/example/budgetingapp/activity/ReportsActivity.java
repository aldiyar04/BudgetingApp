package com.example.budgetingapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.ActivityReportsBinding;


public class ReportsActivity extends AppCompatActivity {
    private ActivityReportsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNav.setSelectedItemId(R.id.reports);
        setBottomNavListeners();
    }

    private void setBottomNavListeners() {
        binding.bottomNav.setOnItemSelectedListener((item) -> {
            if (item.getItemId() == R.id.transactions) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.budgets) {
                startActivity(new Intent(getApplicationContext(), BudgetsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.reports) {
                return true;
            }

            return false;
        });
    }
}