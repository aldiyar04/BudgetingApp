package com.example.budgetingapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;

import com.example.budgetingapp.BudgetingApp;
import com.example.budgetingapp.BudgetingAppDatabase;
import com.example.budgetingapp.dao.AccountDao;
import com.example.budgetingapp.entity.Account;

import java.util.List;
import java.util.concurrent.Executor;

public class AccountVM extends AndroidViewModel {
    private final AccountDao accountDao;
    private final LiveData<List<Account>> accounts;
    private final Executor executor;

    public AccountVM(@NonNull Application application) {
        super(application);
        accountDao = BudgetingAppDatabase.getInstance(application).accountDao();
        accounts = accountDao.findAll();
        executor = BudgetingApp.getExecutor();
    }

    public LiveData<List<Account>> getAllAccounts() {
        return accounts;
    }

    public Account getAccountByName(String name) {
        return accountDao.findByName(name);
    }

    public void save(Account account) {
        executor.execute(() -> accountDao.insert(account));
    }

    public void update(Account account) {
        executor.execute(() -> accountDao.update(account));
    }

    public void delete(Account account) {
        executor.execute(() -> accountDao.delete(account));
    }
}
