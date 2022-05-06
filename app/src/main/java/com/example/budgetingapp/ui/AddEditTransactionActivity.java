package com.example.budgetingapp.ui;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.ActivityAddEditTransactionBinding;
import com.example.budgetingapp.entity.Transaction;
import com.example.budgetingapp.entity.enums.CategoryName;
import com.example.budgetingapp.entity.enums.TransactionType;
import com.example.budgetingapp.ui.adapter.AccountAdapter;
import com.example.budgetingapp.ui.adapter.CategoryAdapter;
import com.example.budgetingapp.viewmodel.AccountVM;
import com.example.budgetingapp.viewmodel.TransactionVM;

import java.util.Objects;
import java.util.stream.IntStream;

public class AddEditTransactionActivity extends AppCompatActivity {
    public static final String EXTRA_ACTIVITY_TYPE = "ActivityType";
    public static final int EXTRA_ACTIVITY_TYPE_ADD = 0;
    public static final int EXTRA_ACTIVITY_TYPE_EDIT_EXPENSE = 1;
    public static final int EXTRA_ACTIVITY_TYPE_EDIT_INCOME = 2;

    public static final String EXTRA_EDITED_TRANSACTION_ID = "EditedTransactionID";

    private static final int MAX_NUMBER_LENGTH = 8;
    private ActivityAddEditTransactionBinding binding;

    private View currentHeaderView;
    private RecyclerView currentCategoryRecyclerView;
    private AccountAdapter accountAdapter;
    private static final int TOP_MARGIN_CATEGORY_RECYCLER_VIEW = 130;
    private static final int START_MARGIN_CATEGORY_RECYCLER_VIEW = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setHeaderAndCategoryRecyclerViewBasedOnActivityType();
        setAccountRecyclerView();
        setNumpadButtonListeners();
        setCloseAndDoneButtonListeners();
    }

    private void setAccountRecyclerView() {
        accountAdapter = new AccountAdapter(this);
        initObservingAccountsByAdapter(accountAdapter);
        RecyclerView accRecyclerView = createRecyclerView(accountAdapter, false);
        addRecyclerViewToConstraintLayout(accRecyclerView,
                405, 10);
    }

    private RecyclerView createRecyclerView(RecyclerView.Adapter<?> recyclerViewAdapter,
                                            boolean hasFixedSize) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false
        );
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(hasFixedSize);
        recyclerView.setAdapter(recyclerViewAdapter);
        return recyclerView;
    }

    private void initObservingAccountsByAdapter(AccountAdapter accAdapter) {
        AccountVM accountVM = new ViewModelProvider(this).get(AccountVM.class);
        accountVM.getAllAccounts().observe(this, accAdapter::setAccounts);
    }


    private void setHeaderAndCategoryRecyclerViewBasedOnActivityType() {
        int activityType = getIntent().getIntExtra(EXTRA_ACTIVITY_TYPE,
                EXTRA_ACTIVITY_TYPE_ADD);

        if (activityType == EXTRA_ACTIVITY_TYPE_ADD) {
            Spinner header = createHeaderSpinner();
            replaceHeaderView(header);
            currentHeaderView = header;
            currentCategoryRecyclerView = createExpenseCategoryRecyclerView();
            addRecyclerViewToConstraintLayout(currentCategoryRecyclerView,
                    TOP_MARGIN_CATEGORY_RECYCLER_VIEW, START_MARGIN_CATEGORY_RECYCLER_VIEW);
            return;
        } else if (activityType == EXTRA_ACTIVITY_TYPE_EDIT_EXPENSE) {
            TextView header = createHeaderTextView("Edit Expense");
            replaceHeaderView(header);
            currentHeaderView = header;
            currentCategoryRecyclerView = createExpenseCategoryRecyclerView();
            addRecyclerViewToConstraintLayout(currentCategoryRecyclerView,
                    TOP_MARGIN_CATEGORY_RECYCLER_VIEW, START_MARGIN_CATEGORY_RECYCLER_VIEW);
            return;
        } else if (activityType == EXTRA_ACTIVITY_TYPE_EDIT_INCOME) {
            TextView header = createHeaderTextView("Edit Income");
            currentHeaderView = header;
            replaceHeaderView(header);
            currentCategoryRecyclerView = createIncomeCategoryRecyclerView();
            addRecyclerViewToConstraintLayout(currentCategoryRecyclerView,
                    TOP_MARGIN_CATEGORY_RECYCLER_VIEW, START_MARGIN_CATEGORY_RECYCLER_VIEW);
            return;
        }
        throw new IllegalStateException("Must provide a valid extra \"IN_EXTRA_ACTIVITY_TYPE\"");
    }

    private RecyclerView createExpenseCategoryRecyclerView() {
        CategoryAdapter expenseCatAdapter = new CategoryAdapter(
                CategoryName.getExpenseCategories(), this
        );
        return createRecyclerView(expenseCatAdapter, true);
    }

    private RecyclerView createIncomeCategoryRecyclerView() {
        CategoryAdapter incomeCatAdapter = new CategoryAdapter(CategoryName.getIncomeCategories(),
                this);
        return createRecyclerView(incomeCatAdapter, true);
    }

    private void addRecyclerViewToConstraintLayout(RecyclerView recyclerView,
                                                   int topMargin,
                                                   int startMargin) {
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
                topMargin);
        constraintSet.connect(recyclerView.getId(), ConstraintSet.START,
                R.id.layoutAddEditTransaction, ConstraintSet.START, startMargin);
        constraintSet.applyTo(constraintLayout);
    }

    private Spinner createHeaderSpinner() {
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.header_spinner_item,
                TransactionType.getStringValueList());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new TransactionTypeSpinnerListener());
        spinner.setTag("HeaderSpinner");
        return spinner;
    }

    private TextView createHeaderTextView(String headerText) {
        TextView textViewHeader = new TextView(this);
        textViewHeader.setText(headerText);
        textViewHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f);
        textViewHeader.setTextColor(Color.BLACK);
        textViewHeader.setTag("HeaderTextView");
        return textViewHeader;
    }

    private void replaceHeaderView(View newHeader) {
        ViewManager parent = binding.header;
        parent.removeView(currentHeaderView);

        // Add new view
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.CENTER;
        parent.addView(newHeader, layoutParams);
    }

    private void replaceCategoryRecyclerView(RecyclerView newRecyclerView) {
        ViewManager parent = binding.layoutAddEditTransaction;
        parent.removeView(currentCategoryRecyclerView);
        addRecyclerViewToConstraintLayout(newRecyclerView,
                TOP_MARGIN_CATEGORY_RECYCLER_VIEW, START_MARGIN_CATEGORY_RECYCLER_VIEW);
    }

    private void setNumpadButtonListeners() {
        setClearButtonListener();
        setDigitButtonListeners();
    }

    private void setClearButtonListener() {
        binding.buttonClear.setOnClickListener(view -> binding.textViewAmount.setText("0"));
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
        CategoryAdapter catAdapter = (CategoryAdapter) currentCategoryRecyclerView.getAdapter();
        String selectedCatName = catAdapter.getSelectedCategoryName();

        if (selectedCatName == null) {
            currentCategoryRecyclerView.setBackgroundColor(Color.RED);
            ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(),
                    Color.RED, Color.WHITE);
            colorAnim.setDuration(1500);
            colorAnim.addUpdateListener(animator -> {
                currentCategoryRecyclerView.setBackgroundColor((int) animator.getAnimatedValue());
            });
            colorAnim.start();
        } else if (isAmountZero()) {
            showToast("Enter a nonzero value");
        } else {
            TransactionVM transactionVM = new ViewModelProvider(this).get(TransactionVM.class);

            String selectedAccName = accountAdapter.getSelectedAccountName();
            long enteredAmount = getAmount();

            int activityType = getIntent().getIntExtra(EXTRA_ACTIVITY_TYPE, -1);
            if (activityType == EXTRA_ACTIVITY_TYPE_ADD) {
                Spinner headerSpinner = binding.layoutAddEditTransaction.findViewWithTag("HeaderSpinner");
                String txTypeStr = headerSpinner.getSelectedItem().toString();

                Transaction newTx = Transaction.builder()
                        .categoryName(selectedCatName)
                        .accountName(selectedAccName)
                        .type(TransactionType.fromString(txTypeStr))
                        .amount(enteredAmount)
                        .build();
                transactionVM.save(newTx);
            } else if (activityType == EXTRA_ACTIVITY_TYPE_EDIT_EXPENSE ||
                    activityType == EXTRA_ACTIVITY_TYPE_EDIT_INCOME) {
                int editedTxID = getIntent().getIntExtra(EXTRA_EDITED_TRANSACTION_ID, -1);
                if (editedTxID == -1) {
                    throw new IllegalStateException("EditedTransactionID extra must be provided");
                }
                Transaction editedTx = transactionVM.getByID(editedTxID);
                if (editedTx == null) {
                    throw new IllegalStateException("Transaction with ID " + editedTxID + " does not exist");
                }
                editedTx.categoryName = selectedCatName;
                editedTx.accountName = selectedAccName;
                editedTx.amount = enteredAmount;
                transactionVM.update(editedTx);
            }
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