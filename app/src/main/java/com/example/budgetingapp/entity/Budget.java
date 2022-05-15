package com.example.budgetingapp.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {
        @Index(value = {"categoryName"}, unique = true)
})
public class Budget {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    public String categoryName;

    public Long spendingMax;

    public Budget(String categoryName, Long spendingMax) {
        this.categoryName = categoryName;
        this.spendingMax = spendingMax;
    }

    public static Budget createMainBudget(Long spendingMax) {
        return new Budget(null, spendingMax);
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
