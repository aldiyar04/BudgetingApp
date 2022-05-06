package com.example.budgetingapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.budgetingapp.entity.Category;
import com.example.budgetingapp.entity.enums.TransactionType;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM Category")
    List<Category> findAll();

    @Query("SELECT * FROM Category WHERE type = 'EXPENSE'")
    List<Category> findAllForExpenses();

    @Query("SELECT * FROM Category WHERE type = 'INCOME'")
    List<Category> findAllForIncome();

    @Query("SELECT * FROM Category WHERE id=:id")
    Category findById(long id);

    @Insert
    void save(Category category);
}
