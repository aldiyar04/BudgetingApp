package com.example.budgetingapp.activity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.example.budgetingapp.BudgetingAppDatabase;
import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.ActivityAddEditTransactionBinding;
import com.example.budgetingapp.entity.Account;
import com.example.budgetingapp.entity.Transaction;
import com.example.budgetingapp.entity.enums.CategoryName;
import com.example.budgetingapp.entity.enums.TransactionType;
import com.example.budgetingapp.adapter.SelectableAccountAdapter;
import com.example.budgetingapp.adapter.SelectableCategoryAdapter;
import com.example.budgetingapp.viewmodel.AccountVM;
import com.example.budgetingapp.viewmodel.TransactionVM;

import java.util.Objects;

public class AddEditTransactionActivity extends AppCompatActivity {
    public static final String EXTRA_ACTIVITY_TYPE = "ActivityType";
    public static final int EXTRA_ACTIVITY_TYPE_ADD = 0;
    public static final int EXTRA_ACTIVITY_TYPE_EDIT_EXPENSE = 1;
    public static final int EXTRA_ACTIVITY_TYPE_EDIT_INCOME = 2;
    public static final String EXTRA_EDITED_TRANSACTION_ID = "EditedTransactionID";

    public static final int RESULT_ADDED = 2;
    public static final int RESULT_EDITED = 3;

    private ActivityAddEditTransactionBinding binding;

    private final HeaderViewManager headerViewManager = new HeaderViewManager();
    private final AccountRecyclerViewManager accountRecyclerViewManager =
            new AccountRecyclerViewManager();
    private final CategoryRecyclerViewManager categoryRecyclerViewManager =
            new CategoryRecyclerViewManager();
    private final TransactionAmountInputManager transactionAmountManager =
            new TransactionAmountInputManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        overridePendingTransition(R.anim.enter_from_right, R.anim.move_left_a_bit);

        setHeaderAndCategoryRecyclerViewBasedOnActivityType();
        accountRecyclerViewManager.addAccountRecyclerViewToLayout();
        transactionAmountManager.setNumpadButtonListeners();
        initEditedTransactionViewValuesIfActivityTypeEdit();
        setCloseAndDoneButtonListeners();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.move_right_a_bit, R.anim.exit_to_right);
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
                        "\"EXTRA_ACTIVITY_TYPE\"");
        }
    }

    private void initEditedTransactionViewValuesIfActivityTypeEdit() {
        int activityType = getIntent().getIntExtra(EXTRA_ACTIVITY_TYPE, EXTRA_ACTIVITY_TYPE_ADD);
        if (activityType == EXTRA_ACTIVITY_TYPE_EDIT_EXPENSE ||
                activityType == EXTRA_ACTIVITY_TYPE_EDIT_INCOME) {
            Transaction editedTx = getEditedTransaction();
            categoryRecyclerViewManager.setSelectedCategory(editedTx.categoryName);
            accountRecyclerViewManager.setSelectedAccount(editedTx.accountId);
            transactionAmountManager.setAmount(editedTx.amount);
        }
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

        CategoryName selectedCategoryName = categoryRecyclerViewManager.getSelectedCategoryName();
        long selectedAccId = accountRecyclerViewManager.getSelectedAccountId();
        long enteredAmount = transactionAmountManager.getAmount();

        int activityResult = RESULT_OK;

        int activityType = getIntent().getIntExtra(EXTRA_ACTIVITY_TYPE, -1);
        if (activityType == EXTRA_ACTIVITY_TYPE_ADD) {
            saveTransactionAndUpdateBalances(selectedCategoryName, selectedAccId, enteredAmount);
            activityResult = RESULT_ADDED;
        } else if (activityType == EXTRA_ACTIVITY_TYPE_EDIT_EXPENSE ||
                activityType == EXTRA_ACTIVITY_TYPE_EDIT_INCOME) {
            updateTransactionAndUpdateBalances(selectedCategoryName, selectedAccId, enteredAmount);
            activityResult = RESULT_EDITED;
        }

        Intent intent = new Intent(AddEditTransactionActivity.this, MainActivity.class);
        setResult(activityResult, intent);
        finish();
    }

    private void saveTransactionAndUpdateBalances(CategoryName categoryName,
                                                  long accountId,
                                                  long amount) {
        TransactionType txType = headerViewManager.getSelectedTransactionTypeFromSpinner();

        Transaction newTx = Transaction.builder()
                .categoryName(categoryName)
                .accountId(accountId)
                .type(txType)
                .amount(amount)
                .build();

        TransactionVM transactionVM = getTransactionVM();
        AccountVM accountVM = getAccountVM();

        BudgetingAppDatabase db = BudgetingAppDatabase.getInstance(AddEditTransactionActivity.this);
        db.runInTransaction(() -> {
            transactionVM.save(newTx);
            Account account = accountVM.getAccountById(accountId);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            long netWorth = sharedPref.getLong("NetWorth", 0L);

            if (txType == TransactionType.EXPENSE) {
                account.balance -= amount;
                netWorth -= amount;
            } else {
                account.balance += amount;
                netWorth += amount;
            }
            accountVM.update(account);
            sharedPref.edit()
                    .putLong("NetWorth", netWorth)
                    .apply();
        });

    }

    private TransactionVM getTransactionVM() {
        return new ViewModelProvider(this).get(TransactionVM.class);
    }

    private AccountVM getAccountVM() {
        return new ViewModelProvider(this).get(AccountVM.class);
    }

    private void updateTransactionAndUpdateBalances(CategoryName newCategoryName,
                                                    long newAccountId,
                                                    long newAmount) {
        Transaction editedTx = getEditedTransaction();
        long oldAccountId = editedTx.accountId;
        long oldAmount = editedTx.amount;

        TransactionVM transactionVM = getTransactionVM();
        AccountVM accountVM = getAccountVM();

        BudgetingAppDatabase db = BudgetingAppDatabase.getInstance(AddEditTransactionActivity.this);
        db.runInTransaction(() -> {
            Account oldAccount = accountVM.getAccountById(oldAccountId);
            Account newAccount = accountVM.getAccountById(newAccountId);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            long netWorth = sharedPref.getLong("NetWorth", 0L);

            long amountDiff = newAmount - oldAmount;

            if (newAccount.equals(oldAccount)) {
                if (editedTx.type == TransactionType.EXPENSE) {
                    newAccount.balance -= amountDiff;
                    netWorth -= amountDiff;
                } else {
                    newAccount.balance += amountDiff;
                    netWorth += amountDiff;
                }
                accountVM.update(newAccount);
                sharedPref.edit()
                        .putLong("NetWorth", netWorth)
                        .apply();
            } else {
                if (editedTx.type == TransactionType.EXPENSE) {
                    newAccount.balance -= newAmount;
                    oldAccount.balance += oldAmount;
                } else {
                    newAccount.balance += newAmount;
                    oldAccount.balance -= oldAmount;
                }
                accountVM.update(oldAccount);
                accountVM.update(newAccount);
            }

            editedTx.categoryName = newCategoryName;
            editedTx.accountId = newAccountId;
            editedTx.amount = newAmount;

            transactionVM.update(editedTx);
        });
    }

    private Transaction getEditedTransaction() {
        TransactionVM transactionVM = getTransactionVM();
        long editedTxID = getEditedTransactionID();
        Transaction editedTx = transactionVM.getByID(editedTxID);
        if (editedTx == null) {
            throw new IllegalStateException("Transaction with ID " + editedTxID + " does not exist");
        }
        return editedTx;
    }

    private long getEditedTransactionID() {
        long editedTxID = getIntent().getLongExtra(EXTRA_EDITED_TRANSACTION_ID, -1);
        if (editedTxID == -1) {
            throw new IllegalStateException("EditedTransactionID extra must be provided");
        }
        return editedTxID;
    }

    private boolean validateInput() {
        CategoryName selectedCategoryName = categoryRecyclerViewManager.getSelectedCategoryName();
        if (selectedCategoryName == null) {
            categoryRecyclerViewManager.showCategorySelectionRequiredAnimation();
            return false;
        } else if (transactionAmountManager.isAmountZero()) {
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
                    R.layout.spinner_item_txtype,
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
        public void onNothingSelected(AdapterView<?> adapterView) {}
    }

    private class AccountRecyclerViewManager {
        private SelectableAccountAdapter accountAdapter;

        void addAccountRecyclerViewToLayout() {
            accountAdapter = new SelectableAccountAdapter(AddEditTransactionActivity.this);
            initObservingAccountsByAdapter();
            RecyclerView accRecyclerView = createRecyclerView(accountAdapter, false);
            addRecyclerViewToConstraintLayout(accRecyclerView,
                    405, 10);
        }

        private void initObservingAccountsByAdapter() {
            AccountVM accountVM = new ViewModelProvider(AddEditTransactionActivity.this)
                    .get(AccountVM.class);
            accountVM.getAllAccounts()
                    .observe(AddEditTransactionActivity.this, accountAdapter::setAccounts);
        }

        void setSelectedAccount(long accountId) {
            accountAdapter.setSelectedAccountId(accountId);
        }

        long getSelectedAccountId() {
            return accountAdapter.getSelectedAccountId();
        }
    }

    private class CategoryRecyclerViewManager {
        private RecyclerView currentCategoryRecyclerView;
        private static final int TOP_MARGIN_CATEGORY_RECYCLER_VIEW = 130;
        private static final int START_MARGIN_CATEGORY_RECYCLER_VIEW = 10;

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
            SelectableCategoryAdapter categoryAdapter = new SelectableCategoryAdapter(categoryNames,
                    AddEditTransactionActivity.this);
            return createRecyclerView(categoryAdapter, true);
        }

        void setSelectedCategory(CategoryName categoryName) {
            SelectableCategoryAdapter catAdapter = (SelectableCategoryAdapter) currentCategoryRecyclerView.getAdapter();
            catAdapter.setSelectedCategoryName(categoryName);
        }

        CategoryName getSelectedCategoryName() {
            SelectableCategoryAdapter catAdapter = (SelectableCategoryAdapter) currentCategoryRecyclerView.getAdapter();
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

    public class TransactionAmountInputManager {
        private static final int MAX_NUMBER_LENGTH = 8;
        private final

        void setNumpadButtonListeners() {
            setClearButtonListener();
            setDigitButtonListeners();
        }

        void setAmount(long amount) {
            binding.textViewAmount.setText(String.valueOf(amount));
        }

        long getAmount() {
            return Long.parseLong(binding.textViewAmount.getText().toString());
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

        boolean isAmountZero() {
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
                Toast.makeText(AddEditTransactionActivity.this,
                        "Max number length reached", Toast.LENGTH_LONG).show();
            }
            binding.textViewAmount.setText(amount);
        }
    }
}