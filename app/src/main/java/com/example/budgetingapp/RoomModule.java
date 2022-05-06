//package com.example.budgetingapp;
//
//import android.content.Context;
//
//import com.example.budgetingapp.dao.BudgetDao;
//
//import dagger.Module;
//import dagger.Provides;
//import dagger.hilt.InstallIn;
//import dagger.hilt.android.components.ActivityComponent;
//import dagger.hilt.android.qualifiers.ApplicationContext;
//
//@Module
//@InstallIn(ActivityComponent.class)
//public class RoomModule {
//    @Provides
//    public BudgetDao budgetDao(@ApplicationContext Context appContext) {
//        return BudgetingAppDatabase.getInstance(appContext)
//                .budgetDao();
//    }
//}
