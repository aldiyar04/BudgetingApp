package com.example.budgetingapp.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.ActivityAddEditTransactionBinding;
import com.example.budgetingapp.entity.enums.CategoryName;
import com.example.budgetingapp.entity.enums.TransactionType;
import com.example.budgetingapp.ui.adapter.CategoryAdapter;

import java.util.Objects;

public class AddEditTransactionActivity extends AppCompatActivity {
    public static final String IN_EXTRA_ACTIVITY_TYPE = "ActivityType";
    public static final int IN_EXTRA_ACTIVITY_TYPE_ADD = 0;
    public static final int IN_EXTRA_ACTIVITY_TYPE_EDIT_EXPENSE = 1;
    public static final int IN_EXTRA_ACTIVITY_TYPE_EDIT_INCOME = 2;

    public static final String OUT_EXTRA_TRANSACTION_AMOUNT = "TransactionAmount";

    private static final int MAX_NUMBER_LENGTH = 8;
    private ActivityAddEditTransactionBinding binding;

    private View currentHeaderView;
    private RecyclerView currentCategoryRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setNumpadButtonListeners();
        setCloseAndDoneButtonListeners();

        setHeaderBasedOnActivityType();
    }

    private void setHeaderBasedOnActivityType() {
        int activityType = getIntent().getIntExtra(IN_EXTRA_ACTIVITY_TYPE,
                IN_EXTRA_ACTIVITY_TYPE_ADD);

        if (activityType == IN_EXTRA_ACTIVITY_TYPE_ADD) {
            Spinner header = createHeaderSpinner();
            replaceHeaderView(header);
            // TODO: set based on tx type preference:
            currentCategoryRecyclerView = createExpenseCategoryRecyclerView();
            addRecyclerViewToConstraintLayout(currentCategoryRecyclerView);
            return;
        } else if (activityType == IN_EXTRA_ACTIVITY_TYPE_EDIT_EXPENSE) {
            TextView header = createHeaderTextView("Edit Expense");
            replaceHeaderView(header);
            currentCategoryRecyclerView = createExpenseCategoryRecyclerView();
            addRecyclerViewToConstraintLayout(currentCategoryRecyclerView);
            return;
        } else if (activityType == IN_EXTRA_ACTIVITY_TYPE_EDIT_INCOME) {
            TextView header = createHeaderTextView("Edit Income");
            replaceHeaderView(header);
            currentCategoryRecyclerView = createIncomeCategoryRecyclerView();
            addRecyclerViewToConstraintLayout(currentCategoryRecyclerView);
            return;
        }
        throw new IllegalStateException("Must provide a valid extra \"IN_EXTRA_ACTIVITY_TYPE\"");
    }

    private RecyclerView createExpenseCategoryRecyclerView() {
        CategoryAdapter expenseCatAdapter = new CategoryAdapter(
                CategoryName.getExpenseCategories(), this
        );
        return createCategoryRecyclerView(expenseCatAdapter);
    }

    private RecyclerView createIncomeCategoryRecyclerView() {
        CategoryAdapter incomeCatAdapter = new CategoryAdapter(CategoryName.getIncomeCategories(),
                this);
        return createCategoryRecyclerView(incomeCatAdapter);
    }

    private RecyclerView createCategoryRecyclerView(CategoryAdapter categoryAdapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false
        );
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(categoryAdapter);
        return recyclerView;
    }

    private void addRecyclerViewToConstraintLayout(RecyclerView recyclerView) {
        recyclerView.setId(View.generateViewId());

        ConstraintLayout constraintLayout = binding.layoutAddEditTransaction;
        ConstraintSet constraintSet = new ConstraintSet();

        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        binding.layoutAddEditTransaction.addView(recyclerView, layoutParams);

        constraintSet.clone(constraintLayout);
        constraintSet.connect(recyclerView.getId(), ConstraintSet.TOP,
                R.id.header, ConstraintSet.BOTTOM,
                100);
        constraintSet.connect(recyclerView.getId(), ConstraintSet.START,
                R.id.layoutAddEditTransaction, ConstraintSet.START, 10);
        constraintSet.applyTo(constraintLayout);
    }

    private Spinner createHeaderSpinner() {
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item,
                TransactionType.getStringValueList());
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new TransactionTypeSpinnerListener());
        return spinner;
    }

    private TextView createHeaderTextView(String headerText) {
        TextView textViewHeader = new TextView(this);
        textViewHeader.setText(headerText);
        textViewHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f);
        textViewHeader.setTextColor(Color.BLACK);
        return textViewHeader;
    }

    private void replaceHeaderView(View newHeader) {
        ViewManager parent = (ViewManager) binding.header;
        parent.removeView(currentHeaderView);

        // Add new view
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER;
        parent.addView(newHeader, layoutParams);
    }

    private void replaceCategoryRecyclerView(RecyclerView newRecyclerView) {
        ViewManager parent = (ViewManager) binding.layoutAddEditTransaction;
        parent.removeView(currentCategoryRecyclerView);
        addRecyclerViewToConstraintLayout(newRecyclerView);
    }

    private void setNumpadButtonListeners() {
        setClearButtonListener();
        setDigitButtonListeners();
    }

    private void setClearButtonListener() {
        binding.buttonClear.setOnClickListener(view -> {
            binding.textViewAmount.setText("0");
        });
    }

    private void setDigitButtonListeners() {
        binding.buttonZero.setOnClickListener(view -> {
            updateAmountText('0');
        });
        binding.buttonOne.setOnClickListener(view -> {
            updateAmountText('1');
        });
        binding.buttonTwo.setOnClickListener(view -> {
            updateAmountText('2');
        });
        binding.buttonThree.setOnClickListener(view -> {
            updateAmountText('3');
        });
        binding.buttonFour.setOnClickListener(view -> {
            updateAmountText('4');
        });
        binding.buttonFive.setOnClickListener(view -> {
            updateAmountText('5');
        });
        binding.buttonSix.setOnClickListener(view -> {
            updateAmountText('6');
        });
        binding.buttonSeven.setOnClickListener(view -> {
            updateAmountText('7');
        });
        binding.buttonEight.setOnClickListener(view -> {
            updateAmountText('8');
        });
        binding.buttonNine.setOnClickListener(view -> {
            updateAmountText('9');
        });
    }

    private void updateAmountText(char digit) {
        if (isAmountZero()) {
            setAmountTextToDigit(digit);
        } else {
            appendDigitToAmountText(digit);
        }
    }

    private boolean isAmountZero() {
        String amount = binding.textViewAmount.getText().toString();
        return Objects.equals(amount, "0");
    }

    private void setAmountTextToDigit(char digit) {
        binding.textViewAmount.setText(String.valueOf(digit));
    }

    private void appendDigitToAmountText(char digit) {
        String amount = binding.textViewAmount.getText().toString();
        if (amount.length() < MAX_NUMBER_LENGTH) {
            amount += digit;
        } else {
            showToast("Max number length reached");
        }
        binding.textViewAmount.setText(amount);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void setCloseAndDoneButtonListeners() {
        binding.imageButtonClose.setOnClickListener(view -> onCloseButtonClick());
        binding.buttonDone.setOnClickListener(view -> onDoneButtonClick());
        binding.imageButtonDone.setOnClickListener(view -> onDoneButtonClick());
    }

    private void onCloseButtonClick() {
        Intent intent = new Intent(AddEditTransactionActivity.this,
                MainActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void onDoneButtonClick() {
        if (isAmountZero()) {
            showToast("Enter a nonzero value");
        } else {
            Intent intent = new Intent(AddEditTransactionActivity.this,
                    MainActivity.class);
            intent.putExtra(OUT_EXTRA_TRANSACTION_AMOUNT, getAmount());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private long getAmount() {
        return Long.parseLong(binding.textViewAmount.getText().toString());
    }

    public class TransactionTypeSpinnerListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String selectedTxType = parent.getItemAtPosition(pos).toString();
            if (selectedTxType.equals(TransactionType.EXPENSE.toString())) {
                RecyclerView expenseCatRecyclerView = createExpenseCategoryRecyclerView();
                replaceCategoryRecyclerView(expenseCatRecyclerView);
                currentCategoryRecyclerView = expenseCatRecyclerView;
            } else if (selectedTxType.equals(TransactionType.INCOME.toString())) {
                RecyclerView incomeCatRecyclerView = createIncomeCategoryRecyclerView();
                replaceCategoryRecyclerView(incomeCatRecyclerView);
                currentCategoryRecyclerView = incomeCatRecyclerView;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    }
}