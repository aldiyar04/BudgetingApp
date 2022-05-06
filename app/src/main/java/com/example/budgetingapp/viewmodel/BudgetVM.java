//package com.example.budgetingapp.viewmodel;
//
//import android.app.Application;
//
//import androidx.annotation.NonNull;
//import androidx.lifecycle.AndroidViewModel;
//import androidx.lifecycle.LiveData;
//
//import com.example.budgetingapp.BudgetingAppDatabase;
//import com.example.budgetingapp.entity.relation.BudgetAndCategory;
//import com.example.budgetingapp.repository.BudgetRepository;
//
//import java.util.List;
//
//public class BudgetVM extends AndroidViewModel {
//    private final BudgetRepository budgetRepository;
//    private final LiveData<List<BudgetAndCategory>> allBudgets;
//
//    public BudgetVM(@NonNull Application application) {
//        super(application);
//        BudgetingAppDatabase db = BudgetingAppDatabase.getInstance(application);
//        budgetRepository = new BudgetRepository(application);
//        allBudgets = budgetRepository.findAll();
//    }
//
//    public LiveData<List<BudgetAndCategory>> findAll() {
//        return allBudgets;
//    }
//}
