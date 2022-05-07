package com.example.budgetingapp.ui.accountingtab;

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
import com.example.budgetingapp.ui.MainActivity;
import com.example.budgetingapp.ui.accountingtab.adapter.AccountAdapter;
import com.example.budgetingapp.ui.accountingtab.adapter.CategoryAdapter;
import com.example.budgetingapp.viewmodel.AccountVM;
import com.example.budgetingapp.viewmodel.TransactionVM;

public class AddEditTransactionActivity extends AppCompatActivity {
    public static final String EXTRA_ACTIVITY_TYPE = "ActivityType";
    public static final int EXTRA_ACTIVITY_TYPE_ADD = 0;
    public static final int EXTRA_ACTIVITY_TYPE_EDIT_EXPENSE = 1;
    public static final int EXTRA_ACTIVITY_TYPE_EDIT_INCOME = 2;

    public static final String EXTRA_EDITED_TRANSACTION_ID = "EditedTransactionID";

    private ActivityAddEditTransactionBinding binding;

    private final HeaderViewManager headerViewManager = new HeaderViewManager();
    private final CategoryRecyclerViewManager categoryRecyclerViewManager =
            new CategoryRecyclerViewManager();
    private TransactionAmountInputManager transactionAmountInputManager;

    private AccountAdapter accountAdapter;
    private static final int TOP_MARGIN_CATEGORY_RECYCLER_VIEW = 130;
    private static final int START_MARGIN_CATEGORY_RECYCLER_VIEW = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        transactionAmountInputManager = new TransactionAmountInputManager(binding, this);

        setHeaderAndCategoryRecyclerViewBasedOnActivityType();
        setAccountRecyclerView();
        setNumpadButtonListeners();
        setCloseAndDoneButtonListeners();
    }


    private void setHeaderAndCategoryRecyclerViewBasedOnActivityType() {
        int activityType = getIntent().getIntExtra(EXTRA_ACTIVITY_TYPE, EXTRA_ACTIVITY_TYPE_ADD);

        switch (activityType) {
            case EXTRA_ACTIVITY_TYPE_ADD: {
                headerViewManager.setHeaderToTransactionTypeSpinner();
                categoryRecyclerViewManager.addCategoryRecyclerViewToLayout(TransactionType.EXPENSE);
                return;
            }
            case EXTRA_ACTIVITY_TYPE_EDIT_EXPENSE: {
                headerViewManager.setHeaderToEditTransactionTextView(TransactionType.EXPENSE);
                categoryRecyclerViewManager.addCategoryRecyclerViewToLayout(TransactionType.EXPENSE);
                return;
            }
            case EXTRA_ACTIVITY_TYPE_EDIT_INCOME: {
                headerViewManager.setHeaderToEditTransactionTextView(TransactionType.INCOME);
                categoryRecyclerViewManager.addCategoryRecyclerViewToLayout(TransactionType.INCOME);
                return;
            }

            default:
                throw new IllegalStateException("Must provide a valid value for extra " +
                        "\"IN_EXTRA_ACTIVITY_TYPE\"");
        }
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

    private void setAccountRecyclerView() {
        accountAdapter = new AccountAdapter(this);
        initObservingAccountsByAdapter(accountAdapter);
        RecyclerView accRecyclerView = createRecyclerView(accountAdapter, false);
        addRecyclerViewToConstraintLayout(accRecyclerView,
                405, 10);
    }

    private void initObservingAccountsByAdapter(AccountAdapter accAdapter) {
        AccountVM accountVM = new ViewModelProvider(this).get(AccountVM.class);
        accountVM.getAllAccounts().observe(this, accAdapter::setAccounts);
    }

    private void setNumpadButtonListeners() {
        transactionAmountInputManager.setNumpadButtonListeners();
    }

    private void setCloseAndDoneButtonListeners() {
        binding.imageButtonClose.setOnClickListener(view -> onCloseButtonClick());
        binding.buttonDone.setOnClickListener(view -> onDoneButtonClick());
        binding.imageButtonDone.setOnClickListener(view -> onDoneButtonClick());
    }

    private void onCloseButtonClick() {
        Intent intent = new Intent(AddEditTransactionActivity.this, MainActivity.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void onDoneButtonClick() {
        if (!validateInput()) {
            return;
        }

        String selectedCategoryName = categoryRecyclerViewManager.getSelectedCategoryName();
        String selectedAccName = accountAdapter.getSelectedAccountName();
        long enteredAmount = transactionAmountInputManager.getEnteredAmount();

        int activityType = getIntent().getIntExtra(EXTRA_ACTIVITY_TYPE, -1);
        if (activityType == EXTRA_ACTIVITY_TYPE_ADD) {
            saveNewTransaction(selectedCategoryName, selectedAccName, enteredAmount);
        } else if (activityType == EXTRA_ACTIVITY_TYPE_EDIT_EXPENSE ||
                activityType == EXTRA_ACTIVITY_TYPE_EDIT_INCOME) {
            updateExistingTransaction(selectedCategoryName, selectedAccName, enteredAmount);
        }

        finish();
    }

    private void saveNewTransaction(String categoryName, String accountName, long amount) {
        TransactionType txType = headerViewManager.getSelectedTransactionTypeFromSpinner();

        Transaction newTx = Transaction.builder()
                .categoryName(categoryName)
                .accountName(accountName)
                .type(txType)
                .amount(amount)
                .build();

        TransactionVM transactionVM = new ViewModelProvider(this).get(TransactionVM.class);
        transactionVM.save(newTx);
    }

    private void updateExistingTransaction(String categoryName, String accountName, long amount) {
        Transaction editedTx = getEditedTransaction();
        editedTx.categoryName = categoryName;
        editedTx.accountName = accountName;
        editedTx.amount = amount;

        TransactionVM transactionVM = new ViewModelProvider(this).get(TransactionVM.class);
        transactionVM.update(editedTx);
    }

    private Transaction getEditedTransaction() {
        TransactionVM transactionVM = new ViewModelProvider(this).get(TransactionVM.class);

        int editedTxID = getIntent().getIntExtra(EXTRA_EDITED_TRANSACTION_ID, -1);
        if (editedTxID == -1) {
            throw new IllegalStateException("EditedTransactionID extra must be provided");
        }
        Transaction editedTx = transactionVM.getByID(editedTxID);
        if (editedTx == null) {
            throw new IllegalStateException("Transaction with ID " + editedTxID + " does not exist");
        }
        return editedTx;
    }

    private boolean validateInput() {
        String selectedCategoryName = categoryRecyclerViewManager.getSelectedCategoryName();
        if (selectedCategoryName == null) {
            categoryRecyclerViewManager.showCategorySelectionRequiredAnimation();
            return false;
        } else if (transactionAmountInputManager.isAmountZero()) {
            Toast.makeText(this, "Enter a nonzero value", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private class HeaderViewManager {
        private View currentHeaderView;

        void setHeaderToTransactionTypeSpinner() {
            Spinner spinnerHeader = createHeaderSpinner();
            replaceHeaderView(spinnerHeader);
            currentHeaderView = spinnerHeader;
        }

        void setHeaderToEditTransactionTextView(TransactionType transactionType) {
            TextView textViewHeader = createHeaderTextView("Edit " + transactionType.toString());
            replaceHeaderView(textViewHeader);
            currentHeaderView = textViewHeader;
        }

        TransactionType getSelectedTransactionTypeFromSpinner() {
            if (currentHeaderView instanceof Spinner) {
                Spinner headerSpinner = (Spinner) currentHeaderView;
                String txTypeStr = headerSpinner.getSelectedItem().toString();
                return TransactionType.fromString(txTypeStr);
            }
            throw new IllegalStateException("This method may be called only " +
                    "when activity type is ADD, i.e., when the header view is set to a " +
                    "transaction type spinner");
        }

        private Spinner createHeaderSpinner() {
            Spinner spinner = new Spinner(AddEditTransactionActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddEditTransactionActivity.this,
                    R.layout.header_spinner_item,
                    TransactionType.getStringValueList());
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new TransactionTypeSpinnerListener());
            return spinner;
        }

        private TextView createHeaderTextView(String headerText) {
            TextView textViewHeader = new TextView(AddEditTransactionActivity.this);
            textViewHeader.setText(headerText);
            textViewHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f);
            textViewHeader.setTextColor(Color.BLACK);
            return textViewHeader;
        }

        private void replaceHeaderView(View newHeaderView) {
            ViewManager parent = binding.header;
            parent.removeView(currentHeaderView);

            // Add new view
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.gravity = Gravity.CENTER;
            parent.addView(newHeaderView, layoutParams);
        }
    }

    private class CategoryRecyclerViewManager {
        private RecyclerView currentCategoryRecyclerView;

        void addCategoryRecyclerViewToLayout(TransactionType transactionType) {
            currentCategoryRecyclerView = createCategoryRecyclerView(transactionType);
            addRecyclerViewToConstraintLayout(currentCategoryRecyclerView,
                    TOP_MARGIN_CATEGORY_RECYCLER_VIEW, START_MARGIN_CATEGORY_RECYCLER_VIEW);
        }

        void replaceCategoryRecyclerView(TransactionType transactionType) {
            RecyclerView newCatRecyclerView = createCategoryRecyclerView(transactionType);
            ViewManager parent = binding.layoutAddEditTransaction;
            parent.removeView(currentCategoryRecyclerView);
            addRecyclerViewToConstraintLayout(newCatRecyclerView,
                    TOP_MARGIN_CATEGORY_RECYCLER_VIEW, START_MARGIN_CATEGORY_RECYCLER_VIEW);
            currentCategoryRecyclerView = newCatRecyclerView;
        }

        private RecyclerView createCategoryRecyclerView(TransactionType transactionType) {
            CategoryName[] categoryNames = CategoryName.getCategoriesOfType(transactionType);
            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryNames,
                    AddEditTransactionActivity.this);
            return createRecyclerView(categoryAdapter, true);
        }

        String getSelectedCategoryName() {
            CategoryAdapter catAdapter = (CategoryAdapter) currentCategoryRecyclerView.getAdapter();
            return catAdapter.getSelectedCategoryName();
        }

        void showCategorySelectionRequiredAnimation() {
            currentCategoryRecyclerView.setBackgroundColor(Color.RED);
            ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(),
                    Color.RED, Color.WHITE);
            colorAnim.setDuration(1500);
            colorAnim.addUpdateListener(animator -> {
                currentCategoryRecyclerView.setBackgroundColor((int) animator.getAnimatedValue());
            });
            colorAnim.start();
        }
    }

    private class TransactionTypeSpinnerListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String selectedTxType = parent.getItemAtPosition(pos).toString();
            if (selectedTxType.equals(TransactionType.EXPENSE.toString())) {
                categoryRecyclerViewManager.replaceCategoryRecyclerView(TransactionType.EXPENSE);
            } else if (selectedTxType.equals(TransactionType.INCOME.toString())) {
                categoryRecyclerViewManager.replaceCategoryRecyclerView(TransactionType.INCOME);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }
}