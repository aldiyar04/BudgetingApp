package com.example.budgetingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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

                Arrays.stream(CategoryName.getExpenseOnlyCategories()).forEach(categoryName -> {
                    categoryDao.insert(new Category(categoryName, TransactionType.EXPENSE));
                });
                Arrays.stream(CategoryName.getIncomeOnlyCategories()).forEach(categoryName -> {
                    categoryDao.insert(new Category(categoryName, TransactionType.INCOME));
                });
                Arrays.stream(CategoryName.getCategoriesBothForIncomeAndExpenses())
                        .forEach(categoryName -> {
                    categoryDao.insert(Category.bothForIncomeAndExpenses(categoryName));
                });


                TransactionDao txDao = appDb.transactionDao();

                // Months back 5
                Transaction tx1 = Transaction.builder()
                        .type(TransactionType.EXPENSE)
                        .accountId(1L)
                        .categoryName(CategoryName.BILLS)
                        .amount(10_000L)
                        .createdOn(getFirstDayOfMonth(5))
                        .build();

                Transaction tx2 = Transaction.builder()
                        .type(TransactionType.INCOME)
                        .accountId(1L)
                        .categoryName(CategoryName.OTHER)
                        .amount(50_000L)
                        .createdOn(getFirstDayOfMonth(5))
                        .build();

                // Months back 4
                Transaction tx3 = Transaction.builder()
                        .type(TransactionType.EXPENSE)
                        .accountId(1L)
                        .categoryName(CategoryName.FOOD_AND_DRINKS)
                        .amount(20_000L)
                        .createdOn(getFirstDayOfMonth(4))
                        .build();

                Transaction tx4 = Transaction.builder()
                        .type(TransactionType.INCOME)
                        .accountId(1L)
                        .categoryName(CategoryName.OTHER)
                        .amount(40_000L)
                        .createdOn(getFirstDayOfMonth(4))
                        .build();

                // Months back 3
                Transaction tx5 = Transaction.builder()
                        .type(TransactionType.EXPENSE)
                        .accountId(1L)
                        .categoryName(CategoryName.BILLS)
                        .amount(60_000L)
                        .createdOn(getFirstDayOfMonth(3))
                        .build();

                Transaction tx6 = Transaction.builder()
                        .type(TransactionType.INCOME)
                        .accountId(1L)
                        .categoryName(CategoryName.OTHER)
                        .amount(30_000L)
                        .createdOn(getFirstDayOfMonth(3))
                        .build();

                // Months back 2
                Transaction tx7 = Transaction.builder()
                        .type(TransactionType.EXPENSE)
                        .accountId(1L)
                        .categoryName(CategoryName.FOOD_AND_DRINKS)
                        .amount(5_000L)
                        .createdOn(getFirstDayOfMonth(2))
                        .build();

                Transaction tx8 = Transaction.builder()
                        .type(TransactionType.INCOME)
                        .accountId(1L)
                        .categoryName(CategoryName.OTHER)
                        .amount(35_000L)
                        .createdOn(getFirstDayOfMonth(2))
                        .build();

                // Months back 1
                Transaction tx9 = Transaction.builder()
                        .type(TransactionType.EXPENSE)
                        .accountId(1L)
                        .categoryName(CategoryName.SPORTS)
                        .amount(12_500L)
                        .createdOn(getFirstDayOfMonth(1))
                        .build();

                Transaction tx10 = Transaction.builder()
                        .type(TransactionType.INCOME)
                        .accountId(1L)
                        .categoryName(CategoryName.OTHER)
                        .amount(52_300L)
                        .createdOn(getFirstDayOfMonth(1))
                        .build();

                // Months back 0
                Transaction tx11 = Transaction.builder()
                        .type(TransactionType.EXPENSE)
                        .accountId(1L)
                        .categoryName(CategoryName.FOOD_AND_DRINKS)
                        .amount(39_000L)
                        .createdOn(LocalDate.now())
                        .build();

                Transaction tx12 = Transaction.builder()
                        .type(TransactionType.INCOME)
                        .accountId(1L)
                        .categoryName(CategoryName.OTHER)
                        .amount(45_000L)
                        .createdOn(LocalDate.now())
                        .build();

                List<Transaction> transactions = Arrays.asList(tx1, tx2, tx3, tx4, tx5, tx6,
                        tx7, tx8, tx9, tx10, tx11, tx12);
                transactions.forEach(BudgetingAppDatabase::saveTransactionAndUpdateBalances);

//                BudgetDao budgetDao = appDb.budgetDao();
//                Budget mainBudget = Budget.createMainBudget(300_000L);
//                budgetDao.insert(mainBudget);
            });
        }
    };

    private static void saveTransactionAndUpdateBalances(Transaction tx) {
        BudgetingAppDatabase appDb = BudgetingAppDatabase.INSTANCE;

        TransactionDao txDao = appDb.transactionDao();
        AccountDao accountDao = appDb.accountDao();

        appDb.runInTransaction(() -> {
            txDao.insert(tx);
            Account account = accountDao.findById(tx.accountId);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(
                    BudgetingApp.getContext()
            );
            long netWorth = sharedPref.getLong("NetWorth", 0L);

            if (tx.type == TransactionType.EXPENSE) {
                account.balance -= tx.amount;
                netWorth -= tx.amount;
            } else {
                account.balance += tx.amount;
                netWorth += tx.amount;
            }
            accountDao.update(account);
            sharedPref.edit()
                    .putLong("NetWorth", netWorth)
                    .apply();
        });
    }

    private static LocalDate getFirstDayOfMonth(int monthsBackFromCurrentMonth) {
        LocalDate todayDate = LocalDate.now();
        return todayDate.withDayOfMonth(1).minusMonths(monthsBackFromCurrentMonth);
    }
}
