package com.example.budgetingapp.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.budgetingapp.entity.enums.TransactionType;

import java.time.LocalDate;

@Entity
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    public Long accountID;

    public Long categoryID;

    public TransactionType type;

    public Long amount;

    public LocalDate createdOn;

    public static Builder builder() {
        return new Transaction().new Builder();
    }

    public class Builder {
        private Builder() {}

        public Builder account(Long accountID) {
            Transaction.this.accountID = accountID;
            return this;
        }

        public Builder category(Long categoryID) {
            Transaction.this.categoryID = categoryID;
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
                ", accountID=" + accountID +
                ", categoryID=" + categoryID +
                ", type=" + type +
                ", amount=" + amount +
                ", createdOn=" + createdOn +
                '}';
    }
}
