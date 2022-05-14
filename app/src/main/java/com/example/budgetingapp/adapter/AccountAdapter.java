package com.example.budgetingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.R;
import com.example.budgetingapp.entity.Account;
import com.example.budgetingapp.helper.KztAmountFormatter;

import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountHolder> {
    private List<Account> accounts = new ArrayList<>();
    private final Context context;

    public AccountAdapter(Context context) {
        this.context = context;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_account, parent, false);
        return new AccountHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    @Override
    public void onBindViewHolder(@NonNull AccountHolder holder, int position) {
        Account account = accounts.get(position);
        holder.accountName.setText(account.name);
        String accBalance = KztAmountFormatter.format(account.balance);
        holder.accountBalance.setText(accBalance);

        int color;
        if (account.balance > 0) {
            color = getColor(R.color.green);
        } else {
            color = getColor(R.color.red);
        }
        holder.accountBalance.setTextColor(color);
    }

    private int getColor(int colorID) {
        return ContextCompat.getColor(AccountAdapter.this.context, colorID);
    }

    class AccountHolder extends RecyclerView.ViewHolder {
        final TextView accountName;
        final TextView accountBalance;

        AccountHolder(@NonNull View itemView) {
            super(itemView);
            accountName = itemView.findViewById(R.id.textViewAccName);
            accountBalance = itemView.findViewById(R.id.textViewAccBalance);
        }
    }
}
