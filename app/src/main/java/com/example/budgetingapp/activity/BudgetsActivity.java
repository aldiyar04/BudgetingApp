package com.example.budgetingapp.activity;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.ActivityBudgetsBinding;
import com.example.budgetingapp.entity.Budget;
import com.example.budgetingapp.entity.Transaction;
import com.example.budgetingapp.helper.KztAmountFormatter;
import com.example.budgetingapp.viewmodel.BudgetVM;

import java.time.LocalDate;
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

        setCreateMainBudgetOnClickListener();
        setAddBudgetOnClickListener();
    }

    private void setCreateMainBudgetOnClickListener() {
        binding.buttonCreateMainBudget.setOnClickListener(view -> {
            Intent intent = new Intent(BudgetsActivity.this,
                    AddEditBudgetActivity.class);
            intent.putExtra(AddEditBudgetActivity.EXTRA_ACTIVITY_TYPE,
                    AddEditBudgetActivity.EXTRA_ACTIVITY_TYPE_CREATE_MAIN_BUDGET);
            startActivity(intent);
        });
    }

    private void setAddBudgetOnClickListener() {
        binding.buttonAddBudget.setOnClickListener(view -> {
            Intent intent = new Intent(BudgetsActivity.this,
                    AddEditBudgetActivity.class);
            intent.putExtra(AddEditBudgetActivity.EXTRA_ACTIVITY_TYPE,
                    AddEditBudgetActivity.EXTRA_ACTIVITY_TYPE_ADD_BUDGET);
            startActivity(intent);
        });
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
        getBudgetVM().getMainBudgetLiveData().observe(this, this::setViewFromMainBudget);
    }

    private BudgetVM getBudgetVM() {
        return new ViewModelProvider(this).get(BudgetVM.class);
    }

    private void setViewFromMainBudget(Budget mainBudget) {
        boolean mainBudgetExists = mainBudget != null;
        setVisibilityOnViews(mainBudgetExists);
        if (!mainBudgetExists) {
            return;
        }

        setMainBudgetEditOnClickListener();

        String spendingMaxFormatted = KztAmountFormatter.format(mainBudget.spendingMax);
        binding.textViewMainBudgetTitle.setText("Monthly " + spendingMaxFormatted);
        setMainBudgetDatesTextView();

        getBudgetVM().getAmountSpentForLastMonth().observe(this, amountSpent -> {
            if (amountSpent == null) {
                amountSpent = 0L;
            }
            setMainBudgetAmountMessage(mainBudget.spendingMax, amountSpent);
            setBudgetSpentProgress(binding.progressBarMainBudget,
                    mainBudget.spendingMax, amountSpent);
        });

        setHorizontalBiasForTodayBarAndTextView();
    }

    private void setVisibilityOnViews(boolean mainBudgetExists) {
        if (mainBudgetExists) {
            binding.mainBudget.setVisibility(View.VISIBLE);
            binding.buttonAddBudget.setVisibility(View.VISIBLE);
            binding.textViewNoBudgets.setVisibility(View.GONE);
            binding.buttonCreateMainBudget.setVisibility(View.GONE);
        } else {
            binding.mainBudget.setVisibility(View.GONE);
            binding.buttonAddBudget.setVisibility(View.GONE);
            binding.textViewNoBudgets.setVisibility(View.VISIBLE);
            binding.buttonCreateMainBudget.setVisibility(View.VISIBLE);
        }
    }

    private void setMainBudgetEditOnClickListener() {
        binding.mainBudget.setOnClickListener(view -> {
            Intent intent = new Intent(BudgetsActivity.this,
                    AddEditBudgetActivity.class);
            intent.putExtra(AddEditBudgetActivity.EXTRA_ACTIVITY_TYPE,
                    AddEditBudgetActivity.EXTRA_ACTIVITY_TYPE_EDIT_MAIN_BUDGET);
            startActivity(intent);
        });
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

    private void setMainBudgetAmountMessage(Long spendingMax, Long amountSpent) {
        long leftToSpend = spendingMax - amountSpent;
        if (leftToSpend < 0) {
            long overspent = -leftToSpend;

            String overspentFormatted = KztAmountFormatter.format(overspent);
            binding.textViewMainBudgetAmount.setText(overspentFormatted);

            String overspentMessage = getOverspentMessage(overspent, spendingMax);
            binding.textViewMainBudgetMessage.setText(overspentMessage);

            setColorOnMainBudgetAmountMessage(getColor(R.color.red));
        } else {
            String leftToSpendFormatted = KztAmountFormatter.format(leftToSpend);
            binding.textViewMainBudgetAmount.setText(leftToSpendFormatted);
            binding.textViewMainBudgetMessage.setText("left to spend");

            if (leftToSpend > 0) {
                setColorOnMainBudgetAmountMessage(getColor(R.color.green));
            } else {
                setColorOnMainBudgetAmountMessage(getColor(R.color.red));
            }
        }
    }

    private String getOverspentMessage(long overspent, long spendingMax) {
        String message = "overspent";
        if (overspent >= spendingMax) {
            int overspentByPercentage = (int) ((double) overspent / spendingMax * 100);
            message += " (by " + overspentByPercentage + "%)";
        }
        return message;
    }

    private void setColorOnMainBudgetAmountMessage(@ColorInt int color) {
        binding.textViewMainBudgetAmount.setTextColor(color);
        binding.textViewMainBudgetMessage.setTextColor(color);
    }

    private void setBudgetSpentProgress(ProgressBar spentProgressBar,
                                        Long spendingMax, Long amountSpent) {
        Long progressRemainder = amountSpent % spendingMax;
        progressRemainder = amountSpent >= spendingMax ? spendingMax : progressRemainder;

        if (spendingMax > Integer.MAX_VALUE || amountSpent > Integer.MAX_VALUE) {
            int progress = (int) ((double) progressRemainder / spendingMax * Integer.MAX_VALUE);
            spentProgressBar.setMax(Integer.MAX_VALUE);
            spentProgressBar.setProgress(progress);
        } else {
            spentProgressBar.setMax(spendingMax.intValue() );
            spentProgressBar.setProgress(progressRemainder.intValue());
        }
    }

    private void setHorizontalBiasForTodayBarAndTextView() {
        float dayOfMonthPercent = (float) getCurrentDayOfMonth() / getNumDaysInCurrentMonth();
        setHorizontalBiasForView(binding.barMainBudgetToday, dayOfMonthPercent);

        float todayTextHorizBias = 0.5f;
        if (dayOfMonthPercent < 0.06) {
            todayTextHorizBias = 0;
        } else if (dayOfMonthPercent > 0.94) {
            todayTextHorizBias = 1;
        }
        setHorizontalBiasForView(binding.textViewMainBudgetToday, todayTextHorizBias);
    }

    private int getCurrentDayOfMonth() {
        return LocalDate.now().getDayOfMonth();
    }

    private int getNumDaysInCurrentMonth() {
        return YearMonth.now().lengthOfMonth();
    }

    private void setHorizontalBiasForView(View view, float horizontalBias) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)
                view.getLayoutParams();
        params.horizontalBias = horizontalBias;
        view.setLayoutParams(params);
    }

    public interface BudgetOnClickCallback {
        void onClick(Budget budget);
    }
}