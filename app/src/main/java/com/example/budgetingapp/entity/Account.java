package com.example.budgetingapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.NumberFormat;
import java.util.Objects;

@Entity
public class Account {
    @PrimaryKey
    @NonNull
    public String name;

    public Long balance;

    public Account(String name, Long balance) {
        this.name = name;
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return name.equals(account.name) && Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
