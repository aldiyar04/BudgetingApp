package com.example.budgetingapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.budgetingapp.entity.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM `Transaction`")
    LiveData<List<Transaction>> findAll();

    @Query("SELECT * FROM `Transaction` tx WHERE tx.id = :id")
    Transaction findByID(long id);

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);
}
