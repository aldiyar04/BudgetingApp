package com.example.budgetingapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.budgetingapp.entity.Transaction;
import com.example.budgetingapp.entity.pojo.CategoryExpense;
import com.example.budgetingapp.entity.pojo.MonthAmount;

import java.util.List;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM `Transaction`")
    LiveData<List<Transaction>> findAll();

    @Query("SELECT * FROM `Transaction` tx WHERE id = :id")
    Transaction findByID(long id);

    @RawQuery(observedEntities = Transaction.class)
    LiveData<List<CategoryExpense>> getExpensesByCategories(SupportSQLiteQuery query);

    @RawQuery(observedEntities = Transaction.class)
    LiveData<List<MonthAmount>> getMonthAmounts(SupportSQLiteQuery query);

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);
}
