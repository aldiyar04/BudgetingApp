package com.example.budgetingapp.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNav.setSelectedItemId(R.id.transactions);
        setBottomNavListeners();
    }

    private void setBottomNavListeners() {
        binding.bottomNav.setOnItemSelectedListener((item) -> {
            if (item.getItemId() == R.id.transactions) {
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
}