package com.example.budgetingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.R;
import com.example.budgetingapp.entity.Account;
import com.example.budgetingapp.entity.Transaction;
import com.example.budgetingapp.entity.enums.TransactionType;
import com.example.budgetingapp.fragment.TransactionsFragment;
import com.example.budgetingapp.helper.KztAmountFormatter;
import com.example.budgetingapp.viewmodel.AccountVM;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionHolder> {
    private List<Transaction> transactions = new ArrayList<>();
    private final TransactionsFragment.TransactionOnClickCallback transactionOnClickCallback;
    private final ComponentActivity parentActivity;
    private final TextView noTransactionsTextView;

    public TransactionAdapter(TransactionsFragment.TransactionOnClickCallback
                                      transactionOnClickCallback,
                              ComponentActivity parentActivity,
                              TextView noTransactionsTextView) {
        this.transactionOnClickCallback = transactionOnClickCallback;
        this.parentActivity = parentActivity;
        this.noTransactionsTextView = noTransactionsTextView;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
        if (transactions.size() > 0) {
            noTransactionsTextView.setVisibility(View.GONE);
        } else {
            noTransactionsTextView.setVisibility(View.VISIBLE);
        }
    }

    public Transaction getTransactionAtPosition(int position) {
        return transactions.get(position);
    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_transaction, parent, false);
        return new TransactionHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolder holder, int position) {
        initHolderFields(holder, position);
        setEditTransactionOnClick(holder, position);
    }

    private void setEditTransactionOnClick(TransactionHolder holder, int position) {
        Transaction tx = transactions.get(position);
        holder.itemView.setOnClickListener(view -> transactionOnClickCallback.onClick(tx));
//        holder.cardViewTransactionItem.setOnTouchListener((view, event) -> {
//            when(event.getAction()) {
//
//            }
//        });
    }

    private void initHolderFields(TransactionHolder holder, int position) {
        Transaction tx = transactions.get(position);

        // set date
        if (position == (getItemCount() - 1) ||
                (position < getItemCount() &&
                        !transactions.get(position + 1).createdOn.equals(tx.createdOn))) {
            String dateStr = getDateString(tx);
            holder.textViewDate.setText(dateStr);
            holder.textViewDate.setVisibility(View.VISIBLE);
        } else {
            holder.textViewDate.setVisibility(View.GONE);
        }

        holder.textViewCategory.setText(tx.categoryName.toString());

        String txAmountSummary = formatTransactionAmountSummary(tx.amount, tx.type);
        holder.textViewAmountSummary.setText(txAmountSummary);

        int color;
        String prefix;
        if (tx.type == TransactionType.EXPENSE) {
            color = getColor(R.color.red);
            prefix = "from ";
        } else {
            color = getColor(R.color.green);
            prefix = "to ";
        }

        holder.textViewAmountSummary.setTextColor(color);
        String fromAccount = prefix + getAccountName(tx.accountId);
        holder.textViewAccount.setText(fromAccount);
    }

    private String getDateString(Transaction tx) {
        String dateStr;
        if (tx.createdOn.equals(LocalDate.now())) {
            dateStr = "Today";
        } else if (tx.createdOn.plusDays(1L).equals(LocalDate.now())) {
            dateStr = "Yesterday";
        } else {
            DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("MMMM dd, yyyy");
            dateStr = dtf.format(tx.createdOn);
        }
        return dateStr;
    }

    private String formatTransactionAmountSummary(long txAmount, TransactionType txType) {
        String txAmountFormatted = KztAmountFormatter.format(txAmount);
        String sign = txType == TransactionType.EXPENSE ? "-" : "+";
        return sign + txAmountFormatted;
    }

    private int getColor(int colorID) {
        return ContextCompat.getColor(TransactionAdapter.this.parentActivity, colorID);
    }

    private String getAccountName(long accountId) {
        AccountVM accountVM = new ViewModelProvider(parentActivity).get(AccountVM.class);
        return accountVM.getAccountById(accountId).name;
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionHolder extends RecyclerView.ViewHolder {
        final TextView textViewDate;
        final CardView cardViewTransactionItem;
        final TextView textViewCategory;
        final TextView textViewAmountSummary;
        final TextView textViewAccount;

        TransactionHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);

            cardViewTransactionItem = itemView.findViewById(R.id.cardViewTransactionItem);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewAmountSummary = itemView.findViewById(R.id.textViewSummary);
            textViewAccount = itemView.findViewById(R.id.textViewAccount);
        }
    }
}
