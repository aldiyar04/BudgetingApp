package com.example.budgetingapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.FragmentExpenseIncomeByMonthBinding;
import com.example.budgetingapp.entity.enums.TransactionType;
import com.example.budgetingapp.entity.pojo.MonthAmount;
import com.example.budgetingapp.viewmodel.TransactionVM;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExpenseIncomeByMonthFragment extends Fragment {
    private FragmentExpenseIncomeByMonthBinding binding;

    public ExpenseIncomeByMonthFragment() {}

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

        getTransactionVM().getMonthAmounts(5)
                .observe(getActivity(), this::refreshBarChart);

        return binding.getRoot();
    }

    private TransactionVM getTransactionVM() {
        return new ViewModelProvider(this).get(TransactionVM.class);
    }

    private void refreshBarChart(List<MonthAmount> monthAmounts) {
        List<MonthAmount> expenseMonthAmounts = filterMonthAmountByType(monthAmounts,
                TransactionType.EXPENSE);
        List<MonthAmount> incomeMonthAmounts = filterMonthAmountByType(monthAmounts,
                TransactionType.INCOME);

        List<BarEntry> expenseEntries = mapToBarEntries(expenseMonthAmounts);;
        List<BarEntry> incomeEntries = mapToBarEntries(incomeMonthAmounts);;

        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expense");
        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");

        expenseDataSet.setColor(getColor(R.color.red));
        incomeDataSet.setColor(getColor(R.color.green));

        BarData data = new BarData(expenseDataSet, incomeDataSet);
        binding.barChart.setData(data);



        List<String> yearMonthLabels = expenseMonthAmounts.stream()
                .map(MonthAmount::getYearMonth)
                .collect(Collectors.toList());
        binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(yearMonthLabels));


        binding.barChart.setDrawBarShadow(false);
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.setPinchZoom(false);
        binding.barChart.setDrawGridBackground(false);
        binding.barChart.getAxisRight().setEnabled(false);

        binding.barChart.setExtraBottomOffset(20);
        binding.barChart.setExtraTopOffset(20);
        binding.barChart.setExtraLeftOffset(10);
        binding.barChart.setExtraRightOffset(10);

        // CENTER X AXIS LABELS: (barSpace + barWidth) * 2 + groupSpace = 1
        float barSpace = -0.3f;
        float barWidth = 0.4f;
        float groupSpace = 0.8f;
        binding.barChart.groupBars(0, groupSpace, barSpace);
        data.setBarWidth(barWidth);
        data.setDrawValues(false);

        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(14);
        xAxis.setGranularity(2);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(0 + data.getGroupWidth(groupSpace, barSpace) * 6);

        YAxis leftAxis = binding.barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setTextSize(12);
        leftAxis.setAxisMinimum(0);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        Legend legend = binding.barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);


        // Horizontal scrolling
//        binding.barChart.setDragEnabled(true);
//        binding.barChart.setVisibleXRangeMaximum(6);

        binding.barChart.invalidate();
    }

    private List<BarEntry> getBarEntriesByType(List<MonthAmount> monthAmounts, TransactionType type) {
        List<MonthAmount> monthAmountsTyped = filterMonthAmountByType(monthAmounts,
                type);
        return mapToBarEntries(monthAmountsTyped);
    }

    private List<MonthAmount> filterMonthAmountByType(List<MonthAmount> monthAmounts,
                                                      TransactionType type) {
        return monthAmounts.stream()
                .filter(monthAmount -> monthAmount.type == type)
                .collect(Collectors.toList());
    }

    private List<BarEntry> mapToBarEntries(List<MonthAmount> monthAmounts) {
        int numMonths = monthAmounts.size();
        return IntStream.range(0, numMonths)
                .mapToObj(i -> new BarEntry(i, monthAmounts.get(i).amount))
                .collect(Collectors.toList());
    }

    private int getColor(@ColorRes int color) {
        return getActivity().getColor(color);
    }
}