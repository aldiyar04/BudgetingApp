package com.example.budgetingapp.adapter;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.R;
import com.example.budgetingapp.activity.BudgetsActivity;
import com.example.budgetingapp.entity.Budget;
import com.example.budgetingapp.helper.KztAmountFormatter;
import com.example.budgetingapp.viewmodel.BudgetVM;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetHolder> {
    private List<Budget> budgets = new ArrayList<>();
    private final ComponentActivity parentActivity;
    private final View viewRemainingCategories;

    private BudgetsActivity.BudgetViewCallback budgetOnClickCallback;
    private BudgetsActivity.BudgetViewCallback budgetOnLongClickCallback;

    public BudgetAdapter(
            ComponentActivity parentActivity,
                         View viewRemainingCategories) {
        this.parentActivity = parentActivity;
        this.viewRemainingCategories = viewRemainingCategories;
    }

    public void setBudgetOnClickCallback(BudgetsActivity.BudgetViewCallback budgetOnClickCallback) {
        this.budgetOnClickCallback = budgetOnClickCallback;
    }

    public void setBudgetOnLongClickCallback(BudgetsActivity.BudgetViewCallback budgetOnLongClickCallback) {
        this.budgetOnLongClickCallback = budgetOnLongClickCallback;
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
        notifyDataSetChanged();
        if (budgets.size() > 0) {
            viewRemainingCategories.setVisibility(View.VISIBLE);
            long remainingOnMainBudget = getBudgetVM().getMainBudget().spendingMax -
                    getBudgetVM().getSpendingMaxSumOfAllCategoryBudgets();
            String remainingFormatted = KztAmountFormatter.format(remainingOnMainBudget);
            TextView textViewRemaining =
                    viewRemainingCategories.findViewById(R.id.textViewRemainingSpendingMax);
            textViewRemaining.setText(remainingFormatted);
        } else {
            viewRemainingCategories.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public BudgetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_budget, parent, false);
        return new BudgetHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetHolder holder, int position) {
        initHolderFields(holder, position);
        Budget budget = budgets.get(position);
        holder.itemView.setOnClickListener(view -> {
            budgetOnClickCallback.handle(holder.itemView, budget);
        });
        holder.itemView.setOnLongClickListener(view ->
                budgetOnLongClickCallback.handle(holder.itemView, budget)
        );
    }

    private void initHolderFields(BudgetHolder holder, int position) {
        Budget budget = budgets.get(position);
        holder.textViewCategory.setText(budget.categoryName.toString());

        String spendingMaxFormatted = KztAmountFormatter.format(budget.spendingMax);
        holder.textViewSpendingMax.setText(spendingMaxFormatted);

        getBudgetVM().getAmountSpentForLastMonth(budget.categoryName)
                .observe(parentActivity, amountSpent -> {
            if (amountSpent == null) {
                amountSpent = 0L;
            }
            setBudgetAmountMessage(holder, budget.spendingMax, amountSpent);
            setBudgetSpentProgress(holder.progressBarSpent,
                    budget.spendingMax, amountSpent);
        });

        setHorizontalBiasForTodayBar(holder);
    }

    private BudgetVM getBudgetVM() {
        return new ViewModelProvider(parentActivity).get(BudgetVM.class);
    }

    private void setBudgetAmountMessage(BudgetHolder holder,
                                        Long spendingMax, Long amountSpent) {
        long leftToSpend = spendingMax - amountSpent;
        if (leftToSpend < 0) {
            long overspent = -leftToSpend;

            String overspentFormatted = KztAmountFormatter.format(overspent);
            holder.textViewAmountMessage.setText(overspentFormatted);

            String overspentMessage = getOverspentMessage(overspent, spendingMax);
            holder.textViewOverspent.setText(overspentMessage);

            holder.textViewAmountMessage.setTextColor(getColor(R.color.dark_red));
            holder.textViewOverspent.setVisibility(View.VISIBLE);
        } else {
            String leftToSpendMsg = KztAmountFormatter.format(leftToSpend) + " left";
            holder.textViewAmountMessage.setText(leftToSpendMsg);

            if (leftToSpend > 0) {
                holder.textViewAmountMessage.setTextColor(getColor(R.color.royal_blue));
            } else {
                holder.textViewAmountMessage.setTextColor(getColor(R.color.dark_red));
            }

            holder.textViewOverspent.setVisibility(View.GONE);
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

    private int getColor(int colorID) {
        return ContextCompat.getColor(BudgetAdapter.this.parentActivity, colorID);
    }

    private void setBudgetSpentProgress(ProgressBar progressBarSpent,
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
        progressBarSpent.setMax(max);

        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(
                progressBarSpent,
                "progress",
                0, progress
        );
        progressAnimator.setDuration(700);
        progressAnimator.start();
    }

    private void setHorizontalBiasForTodayBar(BudgetHolder holder) {
        float dayOfMonthPercent = (float) getCurrentDayOfMonth() / getNumDaysInCurrentMonth();
        setHorizontalBiasForView(holder.barToday, dayOfMonthPercent);
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

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    static class BudgetHolder extends RecyclerView.ViewHolder {
        final TextView textViewCategory;
        final TextView textViewSpendingMax;
        final TextView textViewAmountMessage;
        final TextView textViewOverspent;
        final ProgressBar progressBarSpent;
        final View barToday;

        BudgetHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategory = itemView.findViewById(R.id.textViewBudgetCategory);
            textViewSpendingMax = itemView.findViewById(R.id.textViewBudgetSpendingMax);
            textViewAmountMessage = itemView.findViewById(R.id.textViewBudgetAmountMessage);
            textViewOverspent = itemView.findViewById(R.id.textViewBudgetOverspent);
            progressBarSpent = itemView.findViewById(R.id.progressBarBudget);
            barToday = itemView.findViewById(R.id.barBudgetToday);
        }
    }
}
