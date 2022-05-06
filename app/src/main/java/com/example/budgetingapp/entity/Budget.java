package com.example.budgetingapp.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Budget {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    public String categoryName;

    public Long spendingMax;

    public Budget(String categoryName, Long spendingMax) {
        this.categoryName = categoryName;
        this.spendingMax = spendingMax;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", spendingMax=" + spendingMax +
                '}';
    }
}
