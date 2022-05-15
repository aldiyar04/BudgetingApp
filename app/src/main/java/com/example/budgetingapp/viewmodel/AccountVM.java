package com.example.budgetingapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.budgetingapp.BudgetingApp;
import com.example.budgetingapp.BudgetingAppDatabase;
import com.example.budgetingapp.dao.AccountDao;
import com.example.budgetingapp.entity.Account;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AccountVM extends AndroidViewModel {
    private final AccountDao accountDao;
    private final LiveData<List<Account>> accounts;
    private final ExecutorService executorService;

    public AccountVM(@NonNull Application application) {
        super(application);
        accountDao = BudgetingAppDatabase.getInstance(application).accountDao();
        accounts = accountDao.findAll();
        executorService = BudgetingApp.getExecutorService();
    }

    public LiveData<List<Account>> getAllAccounts() {
        return accounts;
    }

    public Account getAccountById(long id) {
        return accountDao.findById(id);
    }

    public Account getAccountByName(String name) {
        return accountDao.findByName(name);
    }

    public long save(Account account) {
        long id = 0;
        Callable<Long> insertCallable = () -> accountDao.insert(account);
        Future<Long> future = executorService.submit(insertCallable);
        try {
            id = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void update(Account account) {
        executorService.execute(() -> accountDao.update(account));
    }

    public void delete(Account account) {
        executorService.execute(() -> accountDao.delete(account));
    }
}
