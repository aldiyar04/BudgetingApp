package com.example.budgetingapp;

import android.app.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static Executor getExecutor() {
        return executorService;
    }
}
