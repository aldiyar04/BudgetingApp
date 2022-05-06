//package com.example.budgetingapp.repository;
//
//import android.app.Application;
//import android.view.SurfaceControl;
//
//import androidx.lifecycle.LiveData;
//
//import com.example.budgetingapp.BudgetingAppDatabase;
//import com.example.budgetingapp.dao.BudgetDao;
//import com.example.budgetingapp.dao.CategoryDao;
//import com.example.budgetingapp.entity.Budget;
//import com.example.budgetingapp.entity.Category;
//import com.example.budgetingapp.entity.enums.CategoryName;
//import com.example.budgetingapp.entity.enums.TransactionType;
//import com.example.budgetingapp.entity.relation.BudgetAndCategory;
//
//import java.util.List;
//
//public class BudgetRepository {
//    private final BudgetDao budgetDao;
//    private final LiveData<List<BudgetAndCategory>> allBudgets;
//
//    public BudgetRepository(Application application) {
//        BudgetingAppDatabase db = BudgetingAppDatabase.getInstance(application);
//        budgetDao = db.budgetDao();
//
//        CategoryDao categoryDao = db.categoryDao();
//        categoryDao.save(new Category(CategoryName.BILLS, TransactionType.EXPENSE));
//        budgetDao.save(new Budget(1L, 100_000L));
//
//        allBudgets = budgetDao.findAll();
//    }
//
//    public LiveData<List<BudgetAndCategory>> findAll() {
//        return allBudgets;
//    }
//}
