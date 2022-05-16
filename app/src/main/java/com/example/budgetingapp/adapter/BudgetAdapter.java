package com.example.budgetingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.R;
import com.example.budgetingapp.activity.BudgetsActivity;
import com.example.budgetingapp.entity.Budget;
import com.example.budgetingapp.entity.Transaction;
import com.example.budgetingapp.fragment.TransactionsFragment;
import com.example.budgetingapp.viewmodel.AccountVM;

import java.util.ArrayList;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetHolder> {
    private List<Budget> budgets = new ArrayList<>();
    private final BudgetsActivity.BudgetOnClickCallback budgetOnClickCallback;
    private final ComponentActivity parentActivity;

    public BudgetAdapter(BudgetsActivity.BudgetOnClickCallback budgetOnClickCallback,
                              ComponentActivity parentActivity) {
        this.budgetOnClickCallback = budgetOnClickCallback;
        this.parentActivity = parentActivity;
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
        notifyDataSetChanged();
//        if (budgets.size() > 0) {
//            noTransactionsTextView.setVisibility(View.GONE);
//        } else {
//            noTransactionsTextView.setVisibility(View.VISIBLE);
//        }
    }

    @NonNull
    @Override
    public BudgetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_transaction, parent, false);
        return new BudgetHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetHolder holder, int position) {
        initHolderFields(holder, position);
        setEditBudgetOnClick(holder, position);
    }

    private void setEditBudgetOnClick(BudgetHolder holder, int position) {
        Budget budget = budgets.get(position);
        holder.itemView.setOnClickListener(view -> {

        });
    }

    private void initHolderFields(BudgetHolder holder, int position) {
        Budget budget = budgets.get(position);
    }

    private int getColor(int colorID) {
        return ContextCompat.getColor(BudgetAdapter.this.parentActivity, colorID);
    }

    private String getAccountName(long accountId) {
        AccountVM accountVM = new ViewModelProvider(parentActivity).get(AccountVM.class);
        return accountVM.getAccountById(accountId).name;
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    static class BudgetHolder extends RecyclerView.ViewHolder {
        final TextView textViewCategory;
        final TextView textViewSpendingMax;
        final TextView textViewAmountMessage;
        final ProgressBar progressBarSpent;
        final View barToday;

        BudgetHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategory = itemView.findViewById(R.id.textViewBudgetCategory);
            textViewSpendingMax = itemView.findViewById(R.id.textViewBudgetSpendingMax);
            textViewAmountMessage = itemView.findViewById(R.id.textViewBudgetAmountMessage);
            progressBarSpent = itemView.findViewById(R.id.progressBarBudget);
            barToday = itemView.findViewById(R.id.barBudgetToday);
        }
    }
}
