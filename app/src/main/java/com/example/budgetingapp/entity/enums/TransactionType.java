package com.example.budgetingapp.entity.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TransactionType {
    EXPENSE("Expense"), INCOME("Income");

    private final String type;

    TransactionType(String type) {
        this.type = type;
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

    public static TransactionType fromString(String s) {
        return Arrays.stream(TransactionType.values())
                .filter(txType -> txType.toString().equals(s))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No TransactionType with value '" +
                        s + " exists"));
    }
}
