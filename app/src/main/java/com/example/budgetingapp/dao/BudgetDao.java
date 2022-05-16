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
    LiveData<Budget> findMainBudget();

    @Transaction
    @Query("SELECT * FROM Category") // Relations (such as BudgetAndCategory) must be queried from the parent entity.
        // Category is the parent entity, because Budget has a foreign key that references Category.
    LiveData<List<CategoryAndBudget>> findCategoryBudgets();

    @Query("select sum(amount) from `Transaction` where type='EXPENSE' " +
            "and date('now','start of month') <= createdOn")
    LiveData<Long> getAmountSpentForLastMonth();

    @Query("select sum(amount) from `Transaction` where type='EXPENSE' " +
            "and date('now','start of month') <= createdOn and categoryName = :categoryName")
    LiveData<Long> getAmountSpentForLastMonth(CategoryName categoryName);

    @Insert
    void insert(Budget budget);

    @Update
    void update(Budget budget);

    @Delete
    void delete(Budget budget);
}
