package com.example.budgetingapp.ui.accountingtab;

import android.content.Context;
import android.widget.Toast;

import com.example.budgetingapp.databinding.ActivityAddEditTransactionBinding;

import java.util.Objects;

public class TransactionAmountInputManager {
    private static final int MAX_NUMBER_LENGTH = 8;
    private final ActivityAddEditTransactionBinding binding;
    private final Context context;

    public TransactionAmountInputManager(ActivityAddEditTransactionBinding binding,
                                         Context context) {
        this.binding = binding;
        this.context = context;
    }

    void setNumpadButtonListeners() {
        setClearButtonListener();
        setDigitButtonListeners();
    }

    long getEnteredAmount() {
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
            Toast.makeText(context, "Max number length reached", Toast.LENGTH_LONG).show();
        }
        binding.textViewAmount.setText(amount);
    }
}
