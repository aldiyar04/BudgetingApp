package com.example.budgetingapp.helper;

import android.content.SharedPreferences;

public class LongSharedPrefLiveData extends SharedPrefLiveData<Long> {
    public LongSharedPrefLiveData(SharedPreferences sharedPrefs, String key, Long defValue) {
        super(sharedPrefs, key, defValue);
    }

    @Override
    Long getValueFromPreferences(String key, Long defValue) {
        return sharedPrefs.getLong(key, defValue);
    }
}
