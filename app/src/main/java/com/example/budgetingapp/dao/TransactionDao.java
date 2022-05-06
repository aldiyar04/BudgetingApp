package com.example.budgetingapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.budgetingapp.entity.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {
    @androidx.room.Transaction
    @Query("SELECT * FROM `Transaction`")
    List<Transaction> findAll();

    @Insert
    void save(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);
}
