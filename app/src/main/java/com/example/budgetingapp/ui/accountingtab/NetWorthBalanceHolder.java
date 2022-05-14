package com.example.budgetingapp.ui.accountingtab;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NetWorthBalanceHolder {
    private final Context context;
    private final SharedPreferences sharedPref;

    public NetWorthBalanceHolder(Context context) {
        this.context = context;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public long getNetWorthBalance() {
        return sharedPref.getLong("NetWorth", 0L);
    }

    public void setNetWorthBalance(long netWorthBalance) {
        sharedPref.edit()
                .putLong("NetWorth", netWorthBalance)
                .apply();
    }
}
