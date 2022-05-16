package com.example.budgetingapp;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.budgetingapp.dao.AccountDao;
import com.example.budgetingapp.dao.BudgetDao;
import com.example.budgetingapp.dao.CategoryDao;
import com.example.budgetingapp.dao.TransactionDao;
import com.example.budgetingapp.entity.Account;
import com.example.budgetingapp.entity.Budget;
import com.example.budgetingapp.entity.Category;
import com.example.budgetingapp.entity.Transaction;
import com.example.budgetingapp.entity.converter.Converters;
import com.example.budgetingapp.entity.enums.CategoryName;
import com.example.budgetingapp.entity.enums.TransactionType;

import java.util.Arrays;

@Database(entities = {Account.class, Category.class, Transaction.class, Budget.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class BudgetingAppDatabase extends RoomDatabase {
    private static BudgetingAppDatabase INSTANCE;

    public abstract AccountDao accountDao();
    public abstract CategoryDao categoryDao();
    public abstract TransactionDao transactionDao();
    public abstract BudgetDao budgetDao();

    public static synchronized BudgetingAppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    BudgetingAppDatabase.class, "BudgetingApp.DB")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addCallback(dbCreationCallBack)
                    .build();
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback dbCreationCallBack = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            BudgetingApp.getExecutor().execute(() -> {
                BudgetingAppDatabase appDb = BudgetingAppDatabase.INSTANCE;

                AccountDao accountDao = appDb.accountDao();
                CategoryDao categoryDao = appDb.categoryDao();

                Account cashAcc = new Account("Cash", 0L);
                Account bankCardAcc = new Account("Bank Card", 0L);
                accountDao.insert(cashAcc);
                accountDao.insert(bankCardAcc);

                Arrays.stream(CategoryName.getExpenseCategories()).forEach(categoryName -> {
                    categoryDao.insert(new Category(categoryName, TransactionType.EXPENSE));
                });
                Arrays.stream(CategoryName.getIncomeCategories()).forEach(categoryName -> {
                    if (categoryName.isBothForExpensesAndIncome()) {
                        return;
                    }
                    categoryDao.insert(new Category(categoryName, TransactionType.INCOME));
                });

//                BudgetDao budgetDao = appDb.budgetDao();
//                Budget mainBudget = Budget.createMainBudget(300_000L);
//                budgetDao.insert(mainBudget);
            });
        }
    };
}
