package com.example.budgetingapp.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Budget {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    public Long categoryID;

    public Long spendingMax;

    public Budget(Long categoryID, Long spendingMax) {
        this.categoryID = categoryID;
        this.spendingMax = spendingMax;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", categoryID=" + categoryID +
                ", spendingMax=" + spendingMax +
                '}';
    }
}
