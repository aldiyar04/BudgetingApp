package com.example.budgetingapp;

import android.app.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.HiltAndroidApp;
import dagger.hilt.android.components.ActivityComponent;

@HiltAndroidApp
@Module
@InstallIn(ActivityComponent.class)
public class BudgetingApp extends Application {
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Provides
    public Executor executor() {
        return executorService;
    }
}
