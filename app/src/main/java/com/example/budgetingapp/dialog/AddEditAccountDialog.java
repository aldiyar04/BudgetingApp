package com.example.budgetingapp.dialog;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.example.budgetingapp.BudgetingAppDatabase;
import com.example.budgetingapp.R;
import com.example.budgetingapp.databinding.DialogAddEditAccountBinding;
import com.example.budgetingapp.entity.Account;
import com.example.budgetingapp.entity.Transaction;
import com.example.budgetingapp.entity.enums.CategoryName;
import com.example.budgetingapp.entity.enums.TransactionType;
import com.example.budgetingapp.viewmodel.AccountVM;
import com.example.budgetingapp.viewmodel.TransactionVM;

public class AddEditAccountDialog extends Dialog {
    private final ComponentActivity parentActivity;
    private ActivityType activityType;
    private Long editedAccountId;

    private DialogAddEditAccountBinding binding;

    public AddEditAccountDialog(@NonNull ComponentActivity parentActivity) {
        super(parentActivity);
        this.parentActivity = parentActivity;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public void setEditedAccountId(Long editedAccountId) {
        this.editedAccountId = editedAccountId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogAddEditAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setBackgroundAndBorder();
        setHeaderBasedOnActivityType();
        initInputsIfActivityTypeEdit();
        setCancelAndOKBtnListeners();
    }

    private void initInputsIfActivityTypeEdit() {
        if (activityType == ActivityType.EDIT) {
            AccountVM accountVM = new ViewModelProvider(parentActivity).get(AccountVM.class);
            Account editedAcc = accountVM.getAccountById(editedAccountId);

            binding.editTextName.setText(editedAcc.name);
            binding.editTextBalance.setText(String.valueOf(editedAcc.balance));
        }
    }

    private void setBackgroundAndBorder() {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setBackgroundDrawableResource(R.drawable.shape_dialog);
    }

    private void setHeaderBasedOnActivityType() {
        if (activityType == ActivityType.ADD) {
            binding.textViewHeader.setText("Add Account");
        } else if (activityType == ActivityType.EDIT) {
            binding.textViewHeader.setText("Edit Account");
        }
    }

    private void setCancelAndOKBtnListeners() {
        binding.imageButtonClose.setOnClickListener(view -> dismiss());
        binding.imageButtonDone.setOnClickListener(view -> {
            String newAccName = binding.editTextName.getText().toString();
            String newAccBalanceStr = binding.editTextBalance.getText().toString();

            if (!validateInput(newAccName, newAccBalanceStr)) {
                return;
            }

            AccountVM accountVM = new ViewModelProvider(parentActivity).get(AccountVM.class);
            SharedPreferences sharedPref =
                    PreferenceManager.getDefaultSharedPreferences(getContext());
            BudgetingAppDatabase db = BudgetingAppDatabase.getInstance(getContext());

            long newAccBalance = Long.parseLong(newAccBalanceStr);

            if (activityType == ActivityType.ADD) {
                if (accountVM.getAccountByName(newAccName) != null) {
                    String msg = "Account \"" + newAccName + "\" already exists";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                Account account = new Account(newAccName, newAccBalance);
                long accountId = accountVM.save(account);
                createCorrectionTransaction(accountId, newAccBalance);
                long netWorth = sharedPref.getLong("NetWorth", 0L);
                sharedPref.edit()
                        .putLong("NetWorth", netWorth + newAccBalance)
                        .apply();
            } else if (activityType == ActivityType.EDIT) {
                db.runInTransaction(() -> {
                    Account editedAcc = accountVM.getAccountById(editedAccountId);
                    long oldAccBalance = editedAcc.balance;
                    
                    long balanceDiff = newAccBalance - oldAccBalance;
                    createCorrectionTransaction(editedAccountId, balanceDiff);

                    editedAcc.name = newAccName;
                    editedAcc.balance = newAccBalance;
                    accountVM.update(editedAcc);

                    long netWorth = sharedPref.getLong("NetWorth", 0L);
                    sharedPref.edit()
                            .putLong("NetWorth", netWorth + balanceDiff)
                            .apply();
                });
            }
            dismiss();
        });
    }

    private void createCorrectionTransaction(long newAccId, long balanceDiff) {
        TransactionVM txVM = new ViewModelProvider(parentActivity)
                .get(TransactionVM.class);

        if (balanceDiff > 0) {
            Transaction correctionIncomeTx = Transaction.builder()
                    .accountId(newAccId)
                    .categoryName(CategoryName.CORRECTION)
                    .type(TransactionType.INCOME)
                    .amount(balanceDiff)
                    .build();
            txVM.save(correctionIncomeTx);
        } else if (balanceDiff < 0) {
            Transaction correctionExpenseTx = Transaction.builder()
                    .accountId(newAccId)
                    .categoryName(CategoryName.CORRECTION)
                    .type(TransactionType.EXPENSE)
                    .amount(-balanceDiff)
                    .build();
            txVM.save(correctionExpenseTx);
        }
    }

    private boolean validateInput(String accName, String accBalanceStr) {
        if (TextUtils.isEmpty(accName) || TextUtils.isEmpty(accBalanceStr)) {
            Toast.makeText(getContext(),
                    "Enter name and balance", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public enum ActivityType {
        ADD, EDIT;
    }
}
