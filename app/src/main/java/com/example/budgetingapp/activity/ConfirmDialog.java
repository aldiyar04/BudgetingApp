package com.example.budgetingapp.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.budgetingapp.databinding.DialogConfirmBinding;

public class ConfirmDialog extends Dialog {
    private DialogConfirmBinding binding;
    private String confirmTitle;
    private View.OnClickListener okButtonOnClickListener;

    public ConfirmDialog(@NonNull Context context) {
        super(context);
    }

    public void setConfirmTitle(String confirmTitle) {
        this.confirmTitle = confirmTitle;

    }

    public void setOkButtonOnClickListener(View.OnClickListener okButtonOnClickListener) {
        this.okButtonOnClickListener = okButtonOnClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogConfirmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textViewConfirmTitle.setText(confirmTitle);
        binding.buttonOK.setOnClickListener(view -> {
            okButtonOnClickListener.onClick(view);
            dismiss();
        });
        binding.buttonCancel.setOnClickListener(view -> dismiss());
    }
}