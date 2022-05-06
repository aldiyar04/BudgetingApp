package com.example.budgetingapp.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.R;
import com.example.budgetingapp.entity.Account;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountHolder> {
    private List<Account> accounts = new ArrayList<>();
    private int selectedPosition;
    private final Context context;

    public AccountAdapter(Context context) {
        this.context = context;
    }

    public String getSelectedAccountName() {
        return selectedPosition == RecyclerView.NO_POSITION ?
                null :
                accounts.get(selectedPosition).name;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
        notifyDataSetChanged();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String accountName = sharedPref.getString("AccountName", null);
        selectedPosition = IntStream.range(0, accounts.size())
                .filter(i -> accounts.get(i).name.equalsIgnoreCase(accountName))
                .findFirst()
                .orElse(RecyclerView.NO_POSITION);
    }

    @NonNull
    @Override
    public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_account, parent, false);
        return new AccountAdapter.AccountHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    @Override
    public void onBindViewHolder(@NonNull AccountHolder holder, int position) {
        Account account = accounts.get(position);
        holder.accountButton.setText(account.name);
        if (selectedPosition == position) {
            int fireOpal = getColor(R.color.fire_opal);
            holder.accountButton.setBackgroundColor(fireOpal);
            holder.accountButton.setTextColor(Color.WHITE);
        } else {
            int lightBlue = getColor(R.color.light_blue);
            holder.accountButton.setBackgroundColor(lightBlue);
            holder.accountButton.setTextColor(Color.BLACK);
        }
    }

    private int getColor(int colorID) {
        return ContextCompat.getColor(
                AccountAdapter.this.context, colorID
        );
    }

    class AccountHolder extends RecyclerView.ViewHolder {
        final MaterialButton accountButton;

        AccountHolder(@NonNull View itemView) {
            super(itemView);
            accountButton = itemView.findViewById(R.id.buttonAccount);
            accountButton.setOnClickListener(view -> {
                selectedPosition = getLayoutPosition();
                notifyDataSetChanged();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                sharedPref.edit()
                        .putString("AccountName", accountButton.getText().toString())
                        .apply();
            });
        }
    }
}
