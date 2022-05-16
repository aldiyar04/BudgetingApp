package com.example.budgetingapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.budgetingapp.BudgetingApp;
import com.example.budgetingapp.BudgetingAppDatabase;
import com.example.budgetingapp.dao.BudgetDao;
import com.example.budgetingapp.entity.Budget;
import com.example.budgetingapp.entity.enums.CategoryName;
import com.example.budgetingapp.entity.relation.CategoryAndBudget;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executor;

public class BudgetVM extends AndroidViewModel {
    private final BudgetDao budgetDao;
    private final LiveData<Budget> mainBudget;
    private final LiveData<List<CategoryAndBudget>> categoryBudgets;
    private final Executor executor;

    public BudgetVM(@NonNull Application application) {
        super(application);
        budgetDao = BudgetingAppDatabase.getInstance(application).budgetDao();
        mainBudget = budgetDao.findMainBudget();
        categoryBudgets = budgetDao.findCategoryBudgets();
        executor = BudgetingApp.getExecutor();
    }

    public LiveData<Budget> getMainBudget() {
        return mainBudget;
    }

    public LiveData<List<CategoryAndBudget>> getCategoryBudgets() {
        return categoryBudgets;
    }

    public LiveData<Long> getAmountSpentForLastMonth() {
        return budgetDao.getAmountSpentForLastMonth();
    }

    public LiveData<Long> getAmountSpentForLastMonth(CategoryName categoryName) {
        return budgetDao.getAmountSpentForLastMonth(categoryName);
    }

    public void save(Budget budget) {
        executor.execute(() -> budgetDao.insert(budget));
    }

    public void update(Budget budget) {
        executor.execute(() -> budgetDao.update(budget));
    }

    public void delete(Budget budget) {
        executor.execute(() -> budgetDao.delete(budget));
    }
}
