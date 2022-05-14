package com.example.budgetingapp.helper;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

abstract class SharedPrefLiveData<T> extends LiveData<T> {
    final SharedPreferences sharedPrefs;
    final String key;
    final T defValue;

    public SharedPrefLiveData(SharedPreferences sharedPrefs, String key, T defValue) {
        this.sharedPrefs = sharedPrefs;
        this.key = key;
        this.defValue = defValue;
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (SharedPrefLiveData.this.key.equals(key)) {
                setValue(getValueFromPreferences(key, defValue));
            }
        }
    };

    abstract T getValueFromPreferences(String key, T defValue);

    @Override
    protected void onActive() {
        super.onActive();
        setValue(getValueFromPreferences(key, defValue));
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    protected void onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        super.onInactive();
    }
}
