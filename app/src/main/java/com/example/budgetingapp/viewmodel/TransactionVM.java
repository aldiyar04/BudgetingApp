package com.example.budgetingapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.budgetingapp.App;
import com.example.budgetingapp.AppDatabase;
import com.example.budgetingapp.dao.TransactionDao;
import com.example.budgetingapp.entity.Transaction;

import java.util.List;
import java.util.concurrent.Executor;

public class TransactionVM extends AndroidViewModel {
    private final TransactionDao transactionDao;
    private final LiveData<List<Transaction>> transactions;
    private final Executor executor;

    public TransactionVM(@NonNull Application application) {
        super(application);
        transactionDao = AppDatabase.getInstance(application).transactionDao();
        transactions = transactionDao.findAll();
        executor = App.getExecutor();
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return transactions;
    }

    public Transaction getByID(long id) {
        return transactionDao.findByID(id);
    }

    public void save(Transaction transaction) {
        executor.execute(() -> transactionDao.insert(transaction));
    }

    public void update(Transaction transaction) {
        executor.execute(() -> transactionDao.update(transaction));
    }

    public void delete(Transaction transaction) {
        executor.execute(() -> transactionDao.delete(transaction));
    }
}
