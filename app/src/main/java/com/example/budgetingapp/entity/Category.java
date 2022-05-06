package com.example.budgetingapp.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.budgetingapp.entity.enums.CategoryName;
import com.example.budgetingapp.entity.enums.TransactionType;

@Entity
public class Category {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    public CategoryName name;

    public TransactionType type;

    public Category(CategoryName name, TransactionType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
