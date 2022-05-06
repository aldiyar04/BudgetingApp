package com.example.budgetingapp.entity.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.budgetingapp.entity.Budget;
import com.example.budgetingapp.entity.Category;

public class CategoryAndBudget {
    @Embedded
    public Category category;

    @Relation(parentColumn = "id", entityColumn = "categoryID")
    public Budget budget;

    @Override
    public String toString() {
        return "BudgetAndCategory{" +
                "budget=" + budget +
                ", category=" + category +
                '}';
    }
}
