package com.example.budgetingapp.entity.pojo;

import com.example.budgetingapp.entity.enums.CategoryName;

public class CategoryExpense {
    public CategoryName categoryName;
    public Long expenseAmount;

    @Override
    public String toString() {
        return "CategoryExpense{" +
                "categoryName=" + categoryName +
                ", expenseAmount=" + expenseAmount +
                '}';
    }
}
