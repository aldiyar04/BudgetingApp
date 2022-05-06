package com.example.budgetingapp.entity.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TransactionType {
    EXPENSE("Expense"), INCOME("Income");

    private final String type;

    TransactionType(String category) {
        this.type = category;
    }

    @Override
    public String toString() {
        return type;
    }

    public static List<String> getStringValueList() {
        return Arrays.stream(TransactionType.values())
                .map(TransactionType::toString)
                .collect(Collectors.toList());
    }
}
