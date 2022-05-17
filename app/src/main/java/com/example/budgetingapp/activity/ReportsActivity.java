package com.example.budgetingapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.ActivityReportsBinding;
import com.example.budgetingapp.fragment.ExpenseIncomeByMonthFragment;
import com.example.budgetingapp.fragment.ExpensesByCategoryFragment;

import java.util.Arrays;
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
        initReportSpinner();
        if (savedInstanceState == null) {
            initLastReportFragmentAndSpinnerOption();
        }

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

    private void initReportSpinner() {
        List<String> reports = Report.getStringValueList();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_report,
                reports);
        binding.spinnerReport.setAdapter(adapter);
        binding.spinnerReport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


                String reportStr = parent.getItemAtPosition(pos).toString();
                Report report = Report.fromString(reportStr);

                saveLastReportToSharedPref(report);

                switch (report) {
                    case EXPENSES_BY_CATEGORY:
                        replaceFragment(ExpensesByCategoryFragment.class);
                        break;
                    case EXPENSE_INCOME_BY_MONTH:
                        replaceFragment(ExpenseIncomeByMonthFragment.class);
                        break;
                    default:
                        throw new IllegalStateException("Not all Report enum values considered " +
                                "in the switch statement");
                }
            }

            private void saveLastReportToSharedPref(Report report) {
                SharedPreferences sharedPref =
                        PreferenceManager.getDefaultSharedPreferences(ReportsActivity.this);
                sharedPref.edit()
                        .putString("Report", report.toString())
                        .apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }


    private void initLastReportFragmentAndSpinnerOption() {
        Report lastReport = loadLastReportFromSharedPref();
        switch (lastReport) {
            case EXPENSES_BY_CATEGORY:
                initFragment(ExpensesByCategoryFragment.class);
                binding.spinnerReport.setSelection(Report.EXPENSES_BY_CATEGORY.ordinal());
                break;
            case EXPENSE_INCOME_BY_MONTH:
                initFragment(ExpenseIncomeByMonthFragment.class);
                binding.spinnerReport.setSelection(Report.EXPENSE_INCOME_BY_MONTH.ordinal());
                break;
            default:
                throw new IllegalStateException("Not all Report enum values considered " +
                        "in the switch statement");
        }
    }

    private Report loadLastReportFromSharedPref() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String reportStr = sharedPref.getString("Report", Report.EXPENSES_BY_CATEGORY.toString());
        return Report.fromString(reportStr);
    }

    private void initFragment(Class<? extends Fragment> fragmentClass) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragmentContainer, fragmentClass, null)
                .commit();
    }

    private void replaceFragment(Class<? extends Fragment> newFragmentClass) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, newFragmentClass, null)
                .commit();
    }

    private enum Report {
        EXPENSES_BY_CATEGORY("Spending by Category"),
        EXPENSE_INCOME_BY_MONTH("Money Flow by Month");

        private final String type;

        Report(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }

        public static List<String> getStringValueList() {
            return Arrays.stream(Report.values())
                    .map(Report::toString)
                    .collect(Collectors.toList());
        }

        public static Report fromString(String s) {
            return Arrays.stream(Report.values())
                    .filter(report -> report.toString().equals(s))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No Report with value '" +
                            s + " exists"));
        }
    }
}