package com.example.budgetingapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.budgetingapp.entity.Budget;
import com.example.budgetingapp.entity.relation.CategoryAndBudget;

import java.util.List;

@Dao
public interface BudgetDao {
    @Transaction
    @Query("SELECT * FROM Category") // Relations (such as BudgetAndCategory) must be queried from the parent entity.
    // Category is the parent entity, because Budget has a foreign key that references Category.
    List<CategoryAndBudget> findAll();

    @Query("SELECT * FROM Budget b WHERE b.categoryID = null")
    Budget findGenericBudget();

    @Insert
    void save(Budget budget);

    @Update
    void update(Budget budget);

    @Delete
    void delete(Budget budget);
}
