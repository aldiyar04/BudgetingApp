package com.example.budgetingapp.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.FragmentExpenseIncomeByMonthBinding;
import com.example.budgetingapp.databinding.FragmentExpensesByCategoryBinding;
import com.example.budgetingapp.entity.pojo.MonthAmount;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

public class ExpenseIncomeByMonthFragment extends Fragment {
    private FragmentExpenseIncomeByMonthBinding binding;

    public ExpenseIncomeByMonthFragment() {
    }

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
        binding = FragmentExpenseIncomeByMonthBinding.inflate(inflater, container, false);

        refreshBarChart(null);

        return binding.getRoot();
    }

    private void refreshBarChart(List<MonthAmount> monthAmounts) {
        binding.barChartExpenseIncomeByMonth.setDrawBarShadow(false);
        binding.barChartExpenseIncomeByMonth.getDescription().setEnabled(false);
        binding.barChartExpenseIncomeByMonth.setPinchZoom(false);
        binding.barChartExpenseIncomeByMonth.setDrawGridBackground(true);
        // empty labels so that the names are spread evenly
        String[] monthLabels = {"", "Name1", "Name2", "Name3", "Name4", "Name5", ""};
        XAxis xAxis = binding.barChartExpenseIncomeByMonth.getXAxis();
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisMinimum(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(monthLabels));

        YAxis leftAxis = binding.barChartExpenseIncomeByMonth.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setTextSize(12);
        leftAxis.setAxisLineColor(Color.WHITE);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularity(2);
        leftAxis.setLabelCount(8, true);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        binding.barChartExpenseIncomeByMonth.getAxisRight().setEnabled(false);
        binding.barChartExpenseIncomeByMonth.getLegend().setEnabled(false);

        float[] valOne = {10, 20, 30, 40, 50};
        float[] valTwo = {60, 50, 40, 30, 20};
        float[] valThree = {50, 60, 20, 10, 30};

        List<BarEntry> barOne = new ArrayList<>();
        List<BarEntry> barTwo = new ArrayList<>();
        List<BarEntry> barThree = new ArrayList<>();
        for (int i = 0; i < valOne.length; i++) {
            barOne.add(new BarEntry(i, valOne[i]));
            barTwo.add(new BarEntry(i, valTwo[i]));
            barThree.add(new BarEntry(i, valThree[i]));
        }

        BarDataSet set1 = new BarDataSet(barOne, "barOne");
        set1.setColor(Color.BLUE);
        BarDataSet set2 = new BarDataSet(barTwo, "barTwo");
        set2.setColor(Color.MAGENTA);
        BarDataSet set3 = new BarDataSet(barThree, "barTwo");
        set2.setColor(Color.GREEN);

        set1.setHighlightEnabled(false);
        set2.setHighlightEnabled(false);
        set3.setHighlightEnabled(false);
        set1.setDrawValues(false);
        set2.setDrawValues(false);
        set3.setDrawValues(false);

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);
        BarData data = new BarData(dataSets);
        float groupSpace = 0.4f;
        float barSpace = 0f;
        float barWidth = 0.3f;
        // (barSpace + barWidth) * 2 + groupSpace = 1
        data.setBarWidth(barWidth);
        // so that the entire chart is shown when scrolled from right to left
        xAxis.setAxisMaximum(monthLabels.length - 1.1f);
        binding.barChartExpenseIncomeByMonth.setData(data);
        binding.barChartExpenseIncomeByMonth.setScaleEnabled(false);
        binding.barChartExpenseIncomeByMonth.setVisibleXRangeMaximum(6f);
        binding.barChartExpenseIncomeByMonth.groupBars(1f, groupSpace, barSpace);
        binding.barChartExpenseIncomeByMonth.invalidate();
    }
}