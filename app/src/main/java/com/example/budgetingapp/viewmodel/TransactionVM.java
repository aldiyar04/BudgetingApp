package com.example.budgetingapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.example.budgetingapp.BudgetingApp;
import com.example.budgetingapp.BudgetingAppDatabase;
import com.example.budgetingapp.dao.TransactionDao;
import com.example.budgetingapp.entity.Transaction;
import com.example.budgetingapp.entity.pojo.CategoryExpense;

import java.util.List;
import java.util.concurrent.Executor;

public class TransactionVM extends AndroidViewModel {
    private final TransactionDao transactionDao;
    private final LiveData<List<Transaction>> transactions;
    private final Executor executor;

    public TransactionVM(@NonNull Application application) {
        super(application);
        transactionDao = BudgetingAppDatabase.getInstance(application).transactionDao();
        transactions = transactionDao.findAll();
        executor = BudgetingApp.getExecutor();
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return transactions;
    }

    public Transaction getByID(long id) {
        return transactionDao.findByID(id);
    }

    public LiveData<List<CategoryExpense>> getExpensesByCategoriesForAllTime() {
        String queryStr = "select categoryName as categoryName, sum(amount) as expenseAmount " +
                "from `Transaction` " +
                "where type='EXPENSE' " +
                "group by categoryName";
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryStr);
        return transactionDao.getExpensesByCategoriesForAllTime(query);
    }

    public LiveData<List<CategoryExpense>> getExpensesByCategoriesForLastMonth() {
        String queryStr = "select categoryName as categoryName, sum(amount) as expenseAmount " +
                "from `Transaction` " +
                "where type='EXPENSE' and date('now','start of month') <= createdOn " +
                "group by categoryName";
        SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryStr);
        return transactionDao.getExpensesByCategoriesForLastMonth(query);
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
