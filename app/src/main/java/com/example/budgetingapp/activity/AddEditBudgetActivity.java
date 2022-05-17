package com.example.budgetingapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.budgetingapp.R;
import com.example.budgetingapp.adapter.SelectableCategoryAdapter;
import com.example.budgetingapp.databinding.ActivityAddEditBudgetBinding;
import com.example.budgetingapp.entity.Budget;
import com.example.budgetingapp.entity.enums.CategoryName;
import com.example.budgetingapp.entity.enums.TransactionType;
import com.example.budgetingapp.helper.KztAmountFormatter;
import com.example.budgetingapp.viewmodel.BudgetVM;

import java.util.Objects;

public class AddEditBudgetActivity extends AppCompatActivity {
    public static final String EXTRA_ACTIVITY_TYPE = "ActivityType";

    public static final int EXTRA_ACTIVITY_TYPE_CREATE_MAIN_BUDGET = 0;
    public static final int EXTRA_ACTIVITY_TYPE_EDIT_MAIN_BUDGET = 1;

    public static final int EXTRA_ACTIVITY_TYPE_ADD_BUDGET = 2;
    public static final int EXTRA_ACTIVITY_TYPE_EDIT_BUDGET = 3;

    public static final String EXTRA_EDITED_BUDGET_ID = "EditedBudgetID";

    private ActivityAddEditBudgetBinding binding;

    private final AmountInputManager amountInputManager = new AmountInputManager();
    private final CategoryRecyclerViewManager categoryRecyclerViewManager =
            new CategoryRecyclerViewManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        overridePendingTransition(R.anim.enter_from_right, R.anim.move_left_a_bit);

        setHeaderAndCategoryRecyclerViewBasedOnActivityType();
        categoryRecyclerViewManager.initRecyclerView();
        amountInputManager.setNumpadButtonListeners();
        initEditedBudgetViewValuesIfActivityTypeEdit();
        setCloseAndDoneButtonListeners();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.move_right_a_bit, R.anim.exit_to_right);
    }

    private void setHeaderAndCategoryRecyclerViewBasedOnActivityType() {
        int activityType = getIntent().getIntExtra(EXTRA_ACTIVITY_TYPE,
                EXTRA_ACTIVITY_TYPE_CREATE_MAIN_BUDGET);

        switch (activityType) {
            case EXTRA_ACTIVITY_TYPE_CREATE_MAIN_BUDGET: {
                binding.textViewHeaderTitle.setText("Create Main Budget");
                binding.textViewCategory.setText("Category: for all expenses");
                categoryRecyclerViewManager.hideCategoryRecyclerView();
                break;
            }
            case EXTRA_ACTIVITY_TYPE_EDIT_MAIN_BUDGET: {
                binding.textViewHeaderTitle.setText("Edit Main Budget");
                binding.textViewCategory.setText("Category: for all expenses");
                categoryRecyclerViewManager.hideCategoryRecyclerView();
                break;
            }
            case EXTRA_ACTIVITY_TYPE_ADD_BUDGET: {
                binding.textViewHeaderTitle.setText("Add Budget");
                binding.textViewCategory.setText("Category:");
                categoryRecyclerViewManager.showCategoryRecyclerView();
                break;
            }
            case EXTRA_ACTIVITY_TYPE_EDIT_BUDGET: {
                binding.textViewHeaderTitle.setText("Edit Budget");

                String categoryText = "Category: " + getEditedBudget().categoryName.toString();
                binding.textViewCategory.setText(categoryText);

                categoryRecyclerViewManager.hideCategoryRecyclerView();
                break;
            }
        }
    }

    private void initEditedBudgetViewValuesIfActivityTypeEdit() {
        if (!isActivityTypeEdit()) {
            return;
        }

        Budget editedBudget;
        if (isBudgetTypeCategory()) {
            editedBudget = getEditedBudget();
            categoryRecyclerViewManager.setSelectedCategory(editedBudget.categoryName);
        } else {
            editedBudget = getBudgetVM().getMainBudget();
        }
        amountInputManager.setAmount(editedBudget.spendingMax);
    }

    private boolean isActivityTypeEdit() {
        int activityType = getIntent().getIntExtra(EXTRA_ACTIVITY_TYPE,
                EXTRA_ACTIVITY_TYPE_CREATE_MAIN_BUDGET);
        return activityType == EXTRA_ACTIVITY_TYPE_EDIT_MAIN_BUDGET ||
                activityType == EXTRA_ACTIVITY_TYPE_EDIT_BUDGET;
    }

    private void setCloseAndDoneButtonListeners() {
        binding.imageButtonClose.setOnClickListener(view -> finish());
        binding.buttonDone.setOnClickListener(view -> onDoneButtonClick());
        binding.imageButtonDone.setOnClickListener(view -> onDoneButtonClick());
    }

    private void onDoneButtonClick() {
        if (!validateInput()) {
            return;
        }

        CategoryName selectedCategoryName = categoryRecyclerViewManager.getSelectedCategoryName();
        long enteredAmount = amountInputManager.getAmount();

        int activityType = getIntent().getIntExtra(EXTRA_ACTIVITY_TYPE, -1);
        switch (activityType) {
            case EXTRA_ACTIVITY_TYPE_CREATE_MAIN_BUDGET:
                saveMainBudget(enteredAmount);
                break;
            case EXTRA_ACTIVITY_TYPE_EDIT_MAIN_BUDGET:
                updateMainBudget(enteredAmount);
                break;
            case EXTRA_ACTIVITY_TYPE_ADD_BUDGET:
                saveBudget(selectedCategoryName, enteredAmount);
                break;
            case EXTRA_ACTIVITY_TYPE_EDIT_BUDGET:
                updateBudget(enteredAmount);
                break;
        }

        finish();
    }

    private boolean validateInput() {
        CategoryName selectedCategoryName = categoryRecyclerViewManager.getSelectedCategoryName();
        if (isBudgetTypeCategory()) {
            long remainingOnMainBudget = getBudgetVM().getMainBudget().spendingMax -
                    getBudgetVM().getSpendingMaxSumOfAllCategoryBudgets();
            long spendingMax = amountInputManager.getAmount();
            if (spendingMax > remainingOnMainBudget) {
                String remainingFormatted = KztAmountFormatter.format(remainingOnMainBudget);
                String msg = "Only " + remainingFormatted + " remaining\n on the main budget";
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                return false;
            }

            if (selectedCategoryName == null) {
                categoryRecyclerViewManager.showCategorySelectionRequiredAnimation();
                return false;
            }

            if (!isActivityTypeEdit() &&
                    getBudgetVM().getBudgetByCategoryName(selectedCategoryName) != null) {
                String msg = "Budget for category \"" + selectedCategoryName + "\" already exists";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                return false;
            }

            if (amountInputManager.isAmountZero()) {
                Toast.makeText(this, "Enter a nonzero value", Toast.LENGTH_LONG).show();
                return false;
            }
        } else if (isBudgetTypeMain()) {
            if (amountInputManager.isAmountZero()) {
                Toast.makeText(this, "Enter a nonzero value", Toast.LENGTH_LONG).show();
                return false;
            }

            long spendingMaxMain = amountInputManager.getAmount();
            long spendingMaxSumOther = getBudgetVM().getSpendingMaxSumOfAllCategoryBudgets();
            if (spendingMaxMain < spendingMaxSumOther) {
                String spendingMaxSumFormatted = KztAmountFormatter.format(spendingMaxSumOther);
                String msg = "Value cannot be < " + spendingMaxSumFormatted +
                        "\n(sum of category budgets)";
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    private boolean isBudgetTypeCategory() {
        int activityType = getIntent().getIntExtra(EXTRA_ACTIVITY_TYPE,
                EXTRA_ACTIVITY_TYPE_CREATE_MAIN_BUDGET);
        return activityType == EXTRA_ACTIVITY_TYPE_ADD_BUDGET ||
                activityType == EXTRA_ACTIVITY_TYPE_EDIT_BUDGET;
    }

    private boolean isBudgetTypeMain() {
        int activityType = getIntent().getIntExtra(EXTRA_ACTIVITY_TYPE,
                EXTRA_ACTIVITY_TYPE_CREATE_MAIN_BUDGET);
        return activityType == EXTRA_ACTIVITY_TYPE_CREATE_MAIN_BUDGET ||
                activityType == EXTRA_ACTIVITY_TYPE_EDIT_MAIN_BUDGET;
    }

    private void saveMainBudget(long enteredAmount) {
        getBudgetVM().save(Budget.createMainBudget(enteredAmount));
    }

    private void updateMainBudget(long enteredAmount) {
        BudgetVM budgetVM = getBudgetVM();
        Budget mainBudget = budgetVM.getMainBudget();
        mainBudget.spendingMax = enteredAmount;
        getBudgetVM().update(mainBudget);
    }

    private void saveBudget(CategoryName selectedCategoryName, long enteredAmount) {
        getBudgetVM().save(new Budget(selectedCategoryName, enteredAmount));
    }

    private void updateBudget(long enteredAmount) {
        Budget editedBudget = getEditedBudget();
        editedBudget.spendingMax = enteredAmount;
        getBudgetVM().update(editedBudget);
    }

    private Budget getEditedBudget() {
        long editedBudgetID = getEditedBudgetId();
        Budget budget = getBudgetVM().getById(editedBudgetID);
        if (budget == null) {
            throw new IllegalStateException("Budget with ID " + editedBudgetID + " does not exist");
        }
        return budget;
    }

    private long getEditedBudgetId() {
        long editedBudgetId = getIntent().getLongExtra(EXTRA_EDITED_BUDGET_ID, -1);
        if (editedBudgetId == -1) {
            throw new IllegalStateException("EditedTransactionID extra must be provided");
        }
        return editedBudgetId;
    }

    private BudgetVM getBudgetVM() {
        return new ViewModelProvider(this).get(BudgetVM.class);
    }

    private class CategoryRecyclerViewManager {
        void initRecyclerView() {
            CategoryName[] categoryNames = CategoryName.getCategoriesOfType(TransactionType.EXPENSE);
            SelectableCategoryAdapter categoryAdapter = new SelectableCategoryAdapter(categoryNames,
                    AddEditBudgetActivity.this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    AddEditBudgetActivity.this,
                    LinearLayoutManager.HORIZONTAL, false
            );
            binding.recyclerViewCategories.setLayoutManager(layoutManager);
            binding.recyclerViewCategories.setHasFixedSize(true);
            binding.recyclerViewCategories.setAdapter(categoryAdapter);
        }

        void showCategoryRecyclerView() {
            binding.recyclerViewCategories.setVisibility(View.VISIBLE);
        }

        void hideCategoryRecyclerView() {
            binding.recyclerViewCategories.setVisibility(View.GONE);
        }

        void setSelectedCategory(CategoryName categoryName) {
            SelectableCategoryAdapter catAdapter = (SelectableCategoryAdapter)
                    binding.recyclerViewCategories.getAdapter();
            catAdapter.setSelectedCategoryName(categoryName);
        }

        CategoryName getSelectedCategoryName() {
            SelectableCategoryAdapter catAdapter =
                    (SelectableCategoryAdapter) binding.recyclerViewCategories.getAdapter();
            return catAdapter.getSelectedCategoryName();
        }

        void showCategorySelectionRequiredAnimation() {
            binding.recyclerViewCategories.setBackgroundColor(Color.RED);
            ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(),
                    Color.RED, Color.WHITE);
            colorAnim.setDuration(1500);
            colorAnim.addUpdateListener(animator -> {
                binding.recyclerViewCategories.setBackgroundColor((int) animator.getAnimatedValue());
            });
            colorAnim.start();
        }
    }

    public class AmountInputManager {
        private static final int MAX_NUMBER_LENGTH = 8;

        private final void setNumpadButtonListeners() {
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
                Toast.makeText(AddEditBudgetActivity.this,
                        "Max number length reached", Toast.LENGTH_LONG).show();
            }
            binding.textViewAmount.setText(amount);
        }
    }
}