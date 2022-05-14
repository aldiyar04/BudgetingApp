package com.example.budgetingapp.helper;

import java.text.NumberFormat;

public class KztAmountFormatter {
    private KztAmountFormatter() {}

    public static String format(long amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String amountStr = formatter.format(amount);
        if (amount >= 0) {
            // amountStr has "$" in front
            // as well as ".00" at the end.
            return amountStr.substring(1, amountStr.length() - 3) + " KZT";
        }
        // amountStr has "-$" in front
        return "-" + amountStr.substring(2, amountStr.length() -3) + " KZT";
    }
}
