package com.example.budgetingapp.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.budgetingapp.R;
import com.example.budgetingapp.adapter.BudgetAdapter;
import com.example.budgetingapp.databinding.ActivityBudgetsBinding;
import com.example.budgetingapp.dialog.ConfirmDialog;
import com.example.budgetingapp.entity.Budget;
import com.example.budgetingapp.helper.KztAmountFormatter;
import com.example.budgetingapp.viewmodel.BudgetVM;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class BudgetsActivity extends AppCompatActivity {
    public static final int PROGRESS_ANIM_DURATION = 700;
    private ActivityBudgetsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBudgetsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // To remove flickering elements, initially INVISIBLE,
        // later set to VISIBLE in main budget LiveData observer method)
        binding.scrollViewContainer.setVisibility(View.INVISIBLE);

        initBottomNav();
        initObservingMainBudgetByView();
        setCreateMainBudgetOnClickListener();
        setAddBudgetOnClickListener();
        initBudgetRecyclerView();
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
        if (!mainBudgetExists()) {
            setVisibilityOnViews(); // VIEWS VISIBILITY SET HERE
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
            setVisibilityOnViews(); // VIEWS VISIBILITY SET HERE
        });

        setHorizontalBiasForTodayBarAndTextView();
    }

    private void setVisibilityOnViews() {
        if (mainBudgetExists()) {
            binding.scrollViewContainer.setVisibility(View.VISIBLE);
            binding.buttonAddBudget.setVisibility(View.VISIBLE);
            binding.textViewNoBudgets.setVisibility(View.GONE);
            binding.buttonCreateMainBudget.setVisibility(View.GONE);
        } else {
            binding.scrollViewContainer.setVisibility(View.GONE);
            binding.buttonAddBudget.setVisibility(View.GONE);
            binding.textViewNoBudgets.setVisibility(View.VISIBLE);
            binding.buttonCreateMainBudget.setVisibility(View.VISIBLE);
        }
    }

    private boolean mainBudgetExists() {
        Budget mainBudget = getBudgetVM().getMainBudget();
        return mainBudget != null;
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

            setColorOnMainBudgetAmountMessage(getColor(R.color.dark_red));
        } else {
            String leftToSpendFormatted = KztAmountFormatter.format(leftToSpend);
            binding.textViewMainBudgetAmount.setText(leftToSpendFormatted);
            binding.textViewMainBudgetMessage.setText("left to spend");

            if (leftToSpend > 0) {
                setColorOnMainBudgetAmountMessage(getColor(R.color.green));
            } else {
                setColorOnMainBudgetAmountMessage(getColor(R.color.dark_red));
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

        int max;
        int progress;
        if (spendingMax > Integer.MAX_VALUE || amountSpent > Integer.MAX_VALUE) {
            progress = (int) ((double) progressRemainder / spendingMax * Integer.MAX_VALUE);
            max = Integer.MAX_VALUE;
        } else {
            max = spendingMax.intValue();
            progress = progressRemainder.intValue();
        }
        spentProgressBar.setMax(max);

        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(
                spentProgressBar,
                "progress",
                0, progress
        );
        progressAnimator.setDuration(PROGRESS_ANIM_DURATION);
        progressAnimator.start();
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

    private void initBudgetRecyclerView() {
        BudgetVM budgetVM = getBudgetVM();
        BudgetAdapter budgetAdapter = new BudgetAdapter(this,
                binding.cardViewRemainingCategories);
        budgetAdapter.setBudgetOnClickCallback(this::startEditBudgetActivity);
        budgetAdapter.setBudgetOnLongClickCallback(this::startDeleteBudgetConfirmDialog);
        budgetVM.getCategoryBudgetsLiveData().observe(this, budgetAdapter::setBudgets);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        binding.recyclerViewBudgets.setLayoutManager(layoutManager);
        binding.recyclerViewBudgets.setHasFixedSize(true);
        binding.recyclerViewBudgets.setNestedScrollingEnabled(false);
        binding.recyclerViewBudgets.setAdapter(budgetAdapter);
    }

    private boolean startEditBudgetActivity(View budgetView, Budget budget) {
        Intent intent = new Intent(BudgetsActivity.this, AddEditBudgetActivity.class);
        intent.putExtra(AddEditBudgetActivity.EXTRA_ACTIVITY_TYPE,
                AddEditBudgetActivity.EXTRA_ACTIVITY_TYPE_EDIT_BUDGET);
        intent.putExtra(AddEditBudgetActivity.EXTRA_EDITED_BUDGET_ID, budget.id);
        startActivity(intent);
        return true;
    }

    private boolean startDeleteBudgetConfirmDialog(View budgetView, Budget budget) {
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setConfirmTitle("Delete budget?");
        dialog.setOkButtonOnClickListener(view -> {
            getBudgetVM().delete(budget);
        });
        dialog.setOnShowListener(dialogInterface -> {
            budgetView.setBackgroundColor(getColor(R.color.yellow));
        });
        dialog.setOnDismissListener(dialogInterface -> {
            budgetView.setBackgroundColor(getColor(R.color.white));
        });
        dialog.show();
        return true;
    }

    public interface BudgetViewCallback {
        boolean handle(View view, Budget budget);
    }
}