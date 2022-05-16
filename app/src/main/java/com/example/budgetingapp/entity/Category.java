package com.example.budgetingapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.budgetingapp.entity.enums.CategoryName;
import com.example.budgetingapp.entity.enums.TransactionType;

@Entity
public class Category {
    @PrimaryKey
    @NonNull
    public CategoryName name;

    public TransactionType type;

    public Category(CategoryName name, TransactionType type) {
        this.name = name;
        this.type = type;
    }

    public static Category bothForIncomeAndExpenses(CategoryName categoryName) {
        if (!categoryName.isBothForExpensesAndIncome()) {
            throw new IllegalArgumentException("Category '" + categoryName +
                    "' is not both for income and expenses");
        }
        return new Category(categoryName, null);
    }

    @Override
    public String toString() {
        return "Category{" +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
