package com.example.budgetingapp.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.budgetingapp.entity.enums.CategoryName;
import com.example.budgetingapp.entity.enums.TransactionType;

import java.time.LocalDate;

@Entity
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    public Long accountId;

    public CategoryName categoryName;

    public TransactionType type;

    public Long amount;

    public LocalDate createdOn;

    public static Builder builder() {
        return new Transaction().new Builder();
    }

    public class Builder {
        private Builder() {}

        public Builder accountId(Long accountId) {
            Transaction.this.accountId = accountId;
            return this;
        }

        public Builder categoryName(CategoryName categoryName) {
            Transaction.this.categoryName = categoryName;
            return this;
        }

        public Builder type(TransactionType type) {
            Transaction.this.type = type;
            return this;
        }

        public Builder amount(Long amount) {
            Transaction.this.amount = amount;
            return this;
        }

        public Transaction build() {
            Transaction.this.createdOn = LocalDate.now();
            return Transaction.this;
        }
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", accountId='" + accountId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", createdOn=" + createdOn +
                '}';
    }
}
