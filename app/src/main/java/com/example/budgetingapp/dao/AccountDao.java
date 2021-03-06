package com.example.budgetingapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.budgetingapp.entity.Account;

import java.util.List;

@Dao
public interface AccountDao {
    @Query("SELECT * FROM Account")
    LiveData<List<Account>> findAll();

    @Query("SELECT * FROM Account WHERE id = :id")
    Account findById(long id);

    @Query("SELECT * FROM Account WHERE name = :name")
    Account findByName(String name);

    @Insert
    long insert(Account account);

    @Update
    void update(Account account);

    @Delete
    void delete(Account account);
}
