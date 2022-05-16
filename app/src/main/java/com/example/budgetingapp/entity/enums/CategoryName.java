package com.example.budgetingapp.entity.enums;

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
    CORRECTION("Correction"), // Only to be specified by the app itself
    OTHER("Other"),

    // Special value for budgets, not saved to DB
    REMAINING_CATEGORIES("Remaining categories");

    private final String categoryName;

    CategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public static CategoryName[] getCategoriesOfType(TransactionType type) {
        switch (type) {
            case EXPENSE:
                return getExpenseCategoriesForUser();
            case INCOME:
                return getIncomeCategoriesForUser();
        }
        throw new IllegalStateException("There can only be transactions of types '" +
                TransactionType.EXPENSE + "' or '" + TransactionType.INCOME + "'");
    }

    public static CategoryName[] getExpenseCategoriesForUser() {
        return new CategoryName[]{BILLS, FOOD_AND_DRINKS, CLOTHING_AND_FOOTWEAR, TRANSPORTATION,
        HEALTH_AND_PERSONAL_CARE, HOME_AND_UTILITIES, EDUCATION, SPORTS, GIFTS, LEISURE, OTHER};
    }

    public static CategoryName[] getIncomeCategoriesForUser() {
        return new CategoryName[]{SALARY, SCHOLARSHIP, OTHER};
    }

    public static CategoryName[] getExpenseOnlyCategories() {
        return new CategoryName[]{BILLS, FOOD_AND_DRINKS, CLOTHING_AND_FOOTWEAR, TRANSPORTATION,
                HEALTH_AND_PERSONAL_CARE, HOME_AND_UTILITIES, EDUCATION, SPORTS, GIFTS, LEISURE};
    }

    public static CategoryName[] getIncomeOnlyCategories() {
        return new CategoryName[]{SALARY, SCHOLARSHIP};
    }

    public static CategoryName[] getCategoriesBothForIncomeAndExpenses() {
        return new CategoryName[]{CORRECTION, OTHER};
    }

    public boolean isBothForExpensesAndIncome() {
        return this == CORRECTION || this == OTHER;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
