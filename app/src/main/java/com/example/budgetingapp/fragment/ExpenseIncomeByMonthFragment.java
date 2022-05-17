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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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

        // To remove flickering elements, initially INVISIBLE,
        // later set to VISIBLE in refreshPieChart() (LiveData observer method)
        binding.getRoot().setVisibility(View.INVISIBLE);

        getTransactionVM().getMonthAmounts(5)
                .observe(getActivity(), this::refreshBarChart);

        return binding.getRoot();
    }

    private void configureBarChart() {
        binding.barChart.setNoDataText("No chart data available");
        binding.barChart.setNoDataTextColor(getColor(R.color.metallic_seaweed));
        // no data text size:
        binding.barChart.getPaint(Chart.PAINT_INFO).setTextSize(60);
        binding.barChart.setDrawBarShadow(false);
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.setPinchZoom(false);
        binding.barChart.setDoubleTapToZoomEnabled(false);
        binding.barChart.setDrawGridBackground(false);
        binding.barChart.getAxisRight().setEnabled(false);

        binding.barChart.setHighlightFullBarEnabled(false);
//        binding.barChart.setHighlightPerTapEnabled(false);
        binding.barChart.setHighlightPerDragEnabled(false);

        binding.barChart.setExtraBottomOffset(20);
        binding.barChart.setExtraLeftOffset(10);
        binding.barChart.setExtraRightOffset(10);

        XAxis xAxis = binding.barChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(14);
        xAxis.setGranularity(2); // 2 bar types: expense and income
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMinimum(0);

        YAxis leftAxis = binding.barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setTextSize(12);
        leftAxis.setAxisMinimum(0);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        Legend legend = binding.barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);

        // Horizontal scrolling
        binding.barChart.setVisibleXRangeMaximum(6);
        binding.barChart.setDragEnabled(true);
    }

    private TransactionVM getTransactionVM() {
        return new ViewModelProvider(this).get(TransactionVM.class);
    }

    private void refreshBarChart(List<MonthAmount> monthAmounts) {
        binding.getRoot().setVisibility(View.VISIBLE);

        for (int i = 0; i < monthAmounts.size();) {
            MonthAmount currentMA = monthAmounts.get(i);

            if (currentMA.type == TransactionType.EXPENSE) {
                if (i + 1 == monthAmounts.size()) {
                    break;
                }
                MonthAmount nextMA = monthAmounts.get(i + 1);
                if (Objects.equals(currentMA.getYearMonth(), nextMA.getYearMonth())) {
                    // nextMA's type is income, everything OK, proceed
                } else {
                    // insert a matching income MA after current expense MA
                    long zeroAmount = 0;
                    MonthAmount incomeMA = new MonthAmount(currentMA.getYearMonth(), zeroAmount,
                                    TransactionType.INCOME);
                    monthAmounts.add(i + 1, incomeMA);
                }
            } else {
                // no matching expense MA for current income MA, so insert one
                long zeroAmount = 0;
                MonthAmount incomeMA = new MonthAmount(currentMA.getYearMonth(), zeroAmount,
                        TransactionType.EXPENSE);
                monthAmounts.add(i, incomeMA);
            }

            i += 2;
        }
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

        if (!monthAmounts.isEmpty()) {
            BarData data = new BarData(expenseDataSet, incomeDataSet);
            binding.barChart.setData(data);

            List<String> yearMonthLabels = monthAmounts.stream()
                    .map(MonthAmount::getYearMonth)
                    .collect(Collectors.toList());
            binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(yearMonthLabels));

            // CENTER X AXIS LABELS: (barSpace + barWidth) * 2 + groupSpace = 1
            float barSpace = -0.3f;
            float barWidth = 0.4f;
            float groupSpace = 0.9f;
            binding.barChart.groupBars(0, groupSpace, barSpace);
            data.setBarWidth(barWidth);
            data.setDrawValues(false);

            float groupWidth = data.getGroupWidth(groupSpace, barSpace);
            int numBars = Math.max(monthAmounts.size(), 6);
            float xWidth = getXWidth(numBars, groupWidth);
            binding.barChart.getXAxis().setAxisMaximum(xWidth);
            binding.barChart.moveViewToX(xWidth);
        }

        configureBarChart();
        binding.barChart.invalidate();

        binding.barChart.animateY(800, Easing.EaseInOutSine);
    }

    private float getXWidth(int numBars, float groupWidth) {
        return groupWidth * numBars - 1;
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