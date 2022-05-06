package com.example.budgetingapp.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.R;
import com.example.budgetingapp.entity.Transaction;
import com.example.budgetingapp.entity.enums.TransactionType;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionHolder> {
    private List<Transaction> transactions = new ArrayList<>();

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
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
        Transaction tx = transactions.get(position);

        // date
        if (position == 0 || (position > 0 && !transactions.get(position - 1).createdOn.equals(tx.createdOn))) {
            String dateStr = tx.createdOn.toString();
            if (tx.createdOn.equals(LocalDate.now())) {
                dateStr = "Today";
            } else if (tx.createdOn.plusDays(1L).equals(LocalDate.now())) {
                dateStr = "Yesterday";
            }
            holder.textViewDate.setText(dateStr);
        } else {
            holder.textViewDate.setVisibility(View.GONE);
        }

        holder.textViewCategory.setText(tx.categoryName);
        String txSummary = formatTransactionSummary(tx.amount, TransactionType.EXPENSE);
        holder.textViewSummary.setText(txSummary);
        int color;
        String prefix;
        if (tx.type == TransactionType.EXPENSE) {
            color = Color.parseColor("#c42a1b"); // red
            prefix = "from ";
        } else {
            color = Color.parseColor("#36AE7C"); // green
            prefix = "to ";
        }
        holder.textViewSummary.setTextColor(color);
        String fromAccount = prefix + tx.accountName;
        holder.textViewAccount.setText(fromAccount);
    }

    private String formatTransactionSummary(long txAmount, TransactionType txType) {
        String txAmountFormatted = formatTransactionAmount(txAmount);
        String sign = txType == TransactionType.EXPENSE ? "-" : "+";
        return sign + txAmountFormatted + " KZT";
    }

    private String formatTransactionAmount(long txAmount) {
        if (txAmount < 0) {
            txAmount = -txAmount;
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String txAmountFormatted = formatter.format(txAmount);
        // txAmountFormatted has "$" in front
        // as well as ".00" at the end.
        return txAmountFormatted.substring(1, txAmountFormatted.length() - 3);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionHolder extends RecyclerView.ViewHolder {
        public final TextView textViewDate;
        public final TextView textViewCategory;
        public final TextView textViewSummary;
        public final TextView textViewAccount;

        public TransactionHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewSummary = itemView.findViewById(R.id.textViewSummary);
            textViewAccount = itemView.findViewById(R.id.textViewAccount);
        }
    }
}
