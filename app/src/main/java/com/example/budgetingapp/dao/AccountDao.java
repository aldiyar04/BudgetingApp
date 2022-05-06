package com.example.budgetingapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.budgetingapp.entity.Account;

import java.util.List;

@Dao
public interface AccountDao {
    @Transaction
    @Query("SELECT * FROM Account")
    List<Account> findAll();

    @Insert
    void save(Account account);

    @Update
    void update(Account account);

    @Delete
    void delete(Account account);
}
