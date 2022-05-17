package com.example.budgetingapp.fragment;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.ColorRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.FragmentExpensesByCategoryBinding;
import com.example.budgetingapp.entity.pojo.CategoryExpense;
import com.example.budgetingapp.viewmodel.TransactionVM;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExpensesByCategoryFragment extends Fragment {
    private FragmentExpensesByCategoryBinding binding;

    public ExpensesByCategoryFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        binding = FragmentExpensesByCategoryBinding.inflate(inflater, container, false);

        initPeriodSpinner();
        configurePieChart();

        return binding.getRoot();
    }

    private void initPeriodSpinner() {
        String[] periods = {"last month", "all time"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item,
                periods);
        binding.spinnerPeriod.setAdapter(adapter);
        binding.spinnerPeriod.setOnItemSelectedListener(new PeriodSpinnerListener());
    }

    private TransactionVM getTransactionVM() {
        return new ViewModelProvider(this).get(TransactionVM.class);
    }

    private void configurePieChart() {
        binding.pieChartExpensesByCategory.setNoDataText("No chart data available");
        binding.pieChartExpensesByCategory.setNoDataTextColor(getColor(R.color.metallic_seaweed));
        // no data text size:
        binding.pieChartExpensesByCategory.getPaint(Chart.PAINT_INFO).setTextSize(60);

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

    private int getColor(@ColorRes int color) {
        return getActivity().getColor(color);
    }

    private void refreshPieChart(List<CategoryExpense> categoryExpenses) {
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

        if (!entries.isEmpty()) {
            binding.pieChartExpensesByCategory.setData(data);
        }
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
            categoryExpenses.observe(getActivity(),
                    ExpensesByCategoryFragment.this::refreshPieChart);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    }
}