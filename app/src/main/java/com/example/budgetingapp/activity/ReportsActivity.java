package com.example.budgetingapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.ActivityReportsBinding;
import com.example.budgetingapp.entity.pojo.CategoryExpense;
import com.example.budgetingapp.viewmodel.TransactionVM;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ReportsActivity extends AppCompatActivity {
    private ActivityReportsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initBottomNav();
        initPeriodSpinner();
        configurePieChart();
    }

    private void initBottomNav() {
        binding.bottomNav.setSelectedItemId(R.id.reports);
        setBottomNavListeners();
    }

    private void setBottomNavListeners() {
        binding.bottomNav.setOnItemSelectedListener((item) -> {
            if (item.getItemId() == R.id.accounting) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.budgets) {
                startActivity(new Intent(getApplicationContext(), BudgetsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.reports) {
                return true;
            }

            return false;
        });
    }

    private void initPeriodSpinner() {
        String[] periods = {"last month", "all time"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item,
                periods);
        binding.spinnerPeriod.setAdapter(adapter);
        binding.spinnerPeriod.setOnItemSelectedListener(new PeriodSpinnerListener());
    }

    private TransactionVM getTransactionVM() {
        return new ViewModelProvider(this).get(TransactionVM.class);
    }

    private void configurePieChart() {
        binding.pieChartExpensesByCategory.setDrawHoleEnabled(true);
        binding.pieChartExpensesByCategory.setUsePercentValues(true);
        binding.pieChartExpensesByCategory.setEntryLabelTextSize(12);
        binding.pieChartExpensesByCategory.setEntryLabelColor(Color.BLACK);
        binding.pieChartExpensesByCategory.setCenterText("Spending by Category");
        binding.pieChartExpensesByCategory.setCenterTextSize(20);
        binding.pieChartExpensesByCategory.setCenterTextColor(getColor(R.color.metallic_seaweed));
        binding.pieChartExpensesByCategory.getDescription().setEnabled(false);

        // Offsets so that pie chart values don't get cut off
        binding.pieChartExpensesByCategory.setExtraTopOffset(15f);
        binding.pieChartExpensesByCategory.setExtraBottomOffset(15f);
        binding.pieChartExpensesByCategory.setExtraLeftOffset(15f);
        binding.pieChartExpensesByCategory.setExtraRightOffset(15f);

        Legend legend = binding.pieChartExpensesByCategory.getLegend();
        legend.setEnabled(false);
//        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
//        legend.setDrawInside(false);
//        legend.setEnabled(true);
    }

    private void refreshExpensesByCategoryPieChart(List<CategoryExpense> categoryExpenses) {
        long totalExpense = categoryExpenses.stream()
                .mapToLong(categoryExpense -> categoryExpense.expenseAmount)
                .sum();
        List<PieEntry> entries = categoryExpenses.stream()
                .map(categoryExpense ->  {
                    float percentage = (float) categoryExpense.expenseAmount / totalExpense;
                    return new PieEntry(percentage, categoryExpense.categoryName.toString());
                })
                .collect(Collectors.toList());

        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setColors(getPieChartColors());

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(binding.pieChartExpensesByCategory));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        binding.pieChartExpensesByCategory.setData(data);
        binding.pieChartExpensesByCategory.invalidate();

        binding.pieChartExpensesByCategory.animateY(800, Easing.EaseInQuad);
    }

    private List<Integer> getPieChartColors() {
        List<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }
        for (int color: ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }
        return colors;
    }

    private class PeriodSpinnerListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String selectedPeriod = parent.getItemAtPosition(pos).toString();
            LiveData<List<CategoryExpense>> categoryExpenses;
            if (selectedPeriod.equalsIgnoreCase("all time")) {
                categoryExpenses = getTransactionVM().getExpensesByCategoriesForAllTime();
            } else if (selectedPeriod.equalsIgnoreCase("last month")) {
                categoryExpenses = getTransactionVM().getExpensesByCategoriesForLastMonth();
            } else {
                throw new IllegalStateException("Selected period must be either \"all time\" " +
                        "or \"last month\"");
            }
            categoryExpenses.observe(ReportsActivity.this,
                    ReportsActivity.this::refreshExpensesByCategoryPieChart);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    }
}