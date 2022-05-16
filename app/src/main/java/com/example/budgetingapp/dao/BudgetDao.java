package com.example.budgetingapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.budgetingapp.entity.Budget;
import com.example.budgetingapp.entity.enums.CategoryName;
import com.example.budgetingapp.entity.relation.CategoryAndBudget;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface BudgetDao {
    @Query("SELECT * FROM Budget WHERE categoryName is null")
    LiveData<Budget> findMainBudgetLiveData();

    @Query("SELECT * FROM Budget WHERE categoryName is null")
    Budget findMainBudget();

    @Transaction
    @Query("SELECT * FROM Budget where categoryName is not null")
    LiveData<List<Budget>> findCategoryBudgetsLiveData();

    @Query("SELECT * FROM Budget WHERE id = :id")
    Budget findById(long id);

    @Query("SELECT * FROM Budget WHERE categoryName = :categoryName")
    Budget findBudgetByCategoryName(CategoryName categoryName);

    @Query("select sum(amount) from `Transaction` where type='EXPENSE' " +
            "and date('now','start of month') <= createdOn")
    LiveData<Long> getAmountSpentForLastMonth();

    @Query("select sum(amount) from `Transaction` where type='EXPENSE' " +
            "and date('now','start of month') <= createdOn and categoryName = :categoryName")
    LiveData<Long> getAmountSpentForLastMonth(CategoryName categoryName);

    @Query("select sum(spendingMax) from budget where categoryName is not null")
    long getSpendingMaxSumOfAllCategoryBudgets();

    @Insert
    void insert(Budget budget);

    @Update
    void update(Budget budget);

    @Delete
    void delete(Budget budget);
}
