package com.example.budgetingapp.entity.enums;

import java.util.Arrays;

public enum CategoryName {
    // Expense categories:
    BILLS("Bills"),
    FOOD_AND_DRINKS("Food & Drinks"),
    CLOTHING_AND_FOOTWEAR("Clothing & Footwear"),
    TRANSPORTATION("Transportation"),
    HEALTH_AND_PERSONAL_CARE("Health & Personal Care"),
    HOME_AND_UTILITIES("Home & Utilities"),
    EDUCATION("Education"),
    SPORTS("Sports"),
    GIFTS("Gifts"),
    LEISURE("Leisure"),

    // Income categories:
    SALARY("Salary"),
    SCHOLARSHIP("Scholarship"),

    // Both for expenses and income
    OTHER("Other");

    private final String categoryName;

    CategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public static CategoryName[] getCategoriesOfType(TransactionType type) {
        switch (type) {
            case EXPENSE:
                return getExpenseCategories();
            case INCOME:
                return getIncomeCategories();
        }
        throw new IllegalStateException("There can only be transactions of types '" +
                TransactionType.EXPENSE + "' or '" + TransactionType.INCOME + "'");
    }

    public static CategoryName[] getExpenseCategories() {
        return new CategoryName[]{BILLS, FOOD_AND_DRINKS, CLOTHING_AND_FOOTWEAR, TRANSPORTATION,
        HEALTH_AND_PERSONAL_CARE, HOME_AND_UTILITIES, EDUCATION, SPORTS, GIFTS, LEISURE, OTHER};
    }

    public static CategoryName[] getIncomeCategories() {
        return new CategoryName[]{SALARY, SCHOLARSHIP, OTHER};
    }

    public static CategoryName fromString(String s) {
        return Arrays.stream(CategoryName.values())
                .filter(catName -> catName.toString().equals(s))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No CategoryName with value '" +
                        s + " exists"));
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
