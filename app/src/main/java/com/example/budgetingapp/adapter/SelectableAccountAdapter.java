package com.example.budgetingapp.adapter;

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
import java.util.Optional;
import java.util.stream.IntStream;

public class SelectableAccountAdapter
        extends RecyclerView.Adapter<SelectableAccountAdapter.AccountHolder> {
    private List<Account> accounts = new ArrayList<>();
    private Optional<Long> selectedAccountId = Optional.empty();
    private int selectedPosition;
    private final Context context;

    public SelectableAccountAdapter(Context context) {
        this.context = context;
        initSelectedPosition();
    }

    public void setSelectedAccountId(long selectedAccountId) {
        this.selectedAccountId = Optional.of(selectedAccountId);
    }

    public long getSelectedAccountId() {
        return accounts.get(selectedPosition).id;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
        notifyDataSetChanged();
        initSelectedPosition();
    }

    private void initSelectedPosition() {
        if (selectedAccountId.isPresent()) {
            selectedPosition = IntStream.range(0, accounts.size())
                    .filter(i -> accounts.get(i).id.equals(selectedAccountId.get()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Account '"
                            + selectedAccountId + "' is not in AccountAdapter.accounts"));
        } else {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            selectedPosition = sharedPref.getInt("SelectedAccPos", 0);
        }
    }

    @NonNull
    @Override
    public AccountHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_account_button, parent, false);
        return new SelectableAccountAdapter.AccountHolder(itemView);
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
        return ContextCompat.getColor(SelectableAccountAdapter.this.context, colorID);
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
                        .putInt("SelectedAccPos", selectedPosition)
                        .apply();
            });
        }
    }
}
