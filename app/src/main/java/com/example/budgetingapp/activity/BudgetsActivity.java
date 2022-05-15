package com.example.budgetingapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.budgetingapp.R;
import com.example.budgetingapp.adapter.TransactionAdapter;
import com.example.budgetingapp.databinding.ActivityBudgetsBinding;
import com.example.budgetingapp.entity.Budget;
import com.example.budgetingapp.helper.KztAmountFormatter;
import com.example.budgetingapp.viewmodel.BudgetVM;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class BudgetsActivity extends AppCompatActivity {
    private ActivityBudgetsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBudgetsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initBottomNav();
        initObservingMainBudgetByView();
//        if (budgets.size() > 0) {
//            binding.textViewNoBudgets.setVisibility(View.GONE);
//            binding.buttonCreateMainBudget.setVisibility(View.GONE);
//        } else {
//            binding.textViewNoBudgets.setVisibility(View.VISIBLE);
//            binding.buttonCreateMainBudget.setVisibility(View.VISIBLE);
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

    private void initObservingMainBudgetByView() {
        getBudgetVM().getMainBudget().observe(this, this::setViewFromMainBudget);
    }

    private BudgetVM getBudgetVM() {
        return new ViewModelProvider(this).get(BudgetVM.class);
    }

    private void setViewFromMainBudget(Budget mainBudget) {
        if (mainBudget == null) {
            binding.mainBudget.setVisibility(View.GONE);
            return;
        }
        binding.mainBudget.setVisibility(View.VISIBLE);

        String spendingMaxFormatted = KztAmountFormatter.format(mainBudget.spendingMax);
        binding.textViewMainBudgetTitle.setText("Monthly " + spendingMaxFormatted);
        setMainBudgetDatesTextView();

        setMainBudgetAmountSpentAndProgress(mainBudget.spendingMax);
        binding.textViewMainBudgetMessage.setText("left to spend");

        setHorizontalBiasForTodayBarAndTextView();
    }

    private void setMainBudgetDatesTextView() {
        LocalDate firstDayOfCurrentMonth = getFirstDayOfCurrentMonth();
        LocalDate lastDayOfCurrentMonth = getLastDayOfCurrentMonth();
        DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String monthFirstDay = dtf.format(firstDayOfCurrentMonth);
        String monthLastDay = dtf.format(lastDayOfCurrentMonth);
        binding.textViewMainBudgetDates.setText(monthFirstDay + " - " + monthLastDay);
    }

    private LocalDate getFirstDayOfCurrentMonth() {
        LocalDate todayDate = LocalDate.now();
        return todayDate.withDayOfMonth(1);
    }

    private LocalDate getLastDayOfCurrentMonth() {
        LocalDate firstDayOfCurrentMonth = getFirstDayOfCurrentMonth();
        return firstDayOfCurrentMonth.plusMonths(1).minusDays(1);
    }

    private void setMainBudgetAmountSpentAndProgress(long mainBudgetSpendingMax) {
        getBudgetVM().getAmountSpentForLastMonth().observe(this, amountSpent -> {
            if (amountSpent == null) {
                amountSpent = 0L;
            }
            long leftToSpend = mainBudgetSpendingMax - amountSpent;
            if (leftToSpend < 0) {
                leftToSpend = 0;
                binding.textViewMainBudgetAmount.setTextColor(getColor(R.color.red));
                binding.textViewMainBudgetMessage.setTextColor(getColor(R.color.red));
            }
            String leftToSpendFormatted = KztAmountFormatter.format(leftToSpend);
            binding.textViewMainBudgetAmount.setText(leftToSpendFormatted);

            int progress = (int) ((double) amountSpent / mainBudgetSpendingMax * 100);
            binding.progressBarMainBudget.setProgress(progress);
            binding.progressBarMainBudget.setMax(100);
        });
    }

    private void setHorizontalBiasForTodayBarAndTextView() {
        float dayOfMonthPercent = (float) getCurrentDayOfMonth() / getNumDaysInCurrentMonth();
        setHorizontalBiasForView(binding.barMainBudgetToday, dayOfMonthPercent);

//        float todayTextHorizBias = 0.5f;
//        if (dayOfMonthPercent < 0.06) {
//            todayTextHorizBias = 0;
//        } else if (dayOfMonthPercent > 0.94) {
//            todayTextHorizBias = 1;
//        }
//        setHorizontalBiasForView(binding.textViewMainBudgetToday, todayTextHorizBias);
    }

    private int getCurrentDayOfMonth() {
        return LocalDate.now().getDayOfMonth();
    }

    private void setHorizontalBiasForView(View view, float horizontalBias) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)
                view.getLayoutParams();
        params.horizontalBias = horizontalBias;
        binding.textViewMainBudgetToday.setLayoutParams(params);
    }

    private int getNumDaysInCurrentMonth() {
        return YearMonth.now().lengthOfMonth();
    }
}