package com.example.budgetingapp;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BudgetingApp extends Application {
    private static BudgetingApp instance;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static Executor getExecutor() {
        return executorService;
    }

    public static ExecutorService getExecutorService() {
        return executorService;
    }

    public static Context getContext(){
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
