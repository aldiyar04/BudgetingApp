package com.example.budgetingapp.entity.pojo;

import com.example.budgetingapp.entity.enums.TransactionType;

import java.text.DateFormatSymbols;

public class MonthAmount {
    private String yearMonth;
    public Long amount;
    public TransactionType type;

    public MonthAmount() {}

    public MonthAmount(String yearMonth, Long amount, TransactionType type) {
        this.yearMonth = yearMonth;
        this.amount = amount;
        this.type = type;
    }

    public void setYearMonth(String yearMonth) {
        String year = yearMonth.substring(0, 4);
        String month = yearMonth.substring(4);
        month = getMonthName(Integer.parseInt(month));
        this.yearMonth = month + " " + year;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    private String getMonthName(int month) {
        DateFormatSymbols symbols = new DateFormatSymbols();
        String[] monthNames = symbols.getMonths();
        return monthNames[month - 1].substring(0, 3);
    }
}
