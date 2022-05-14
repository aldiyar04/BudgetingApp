package com.example.budgetingapp.ui.accountingtab.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budgetingapp.R;
import com.example.budgetingapp.entity.enums.CategoryName;
import com.google.android.material.button.MaterialButton;

import java.util.stream.IntStream;

public class SelectableCategoryAdapter extends RecyclerView.Adapter<SelectableCategoryAdapter.CategoryHolder> {
    private final CategoryName[] categoryNames;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private final Context context;

    public SelectableCategoryAdapter(CategoryName[] categoryNames, Context context) {
        this.categoryNames = categoryNames;
        this.context = context;
    }

    public void setSelectedCategoryName(CategoryName categoryName) {
        selectedPosition = IntStream.range(0, categoryNames.length)
                .filter(i -> categoryNames[i].equals(categoryName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("CategoryName '" +
                        categoryName.toString() + "' is not in CategoryAdapter.categoryNames. " +
                        "Category type for the category name argument must be 'Expense' for " +
                        "expense category adapter, or 'Income' for income one."));
    }

    public CategoryName getSelectedCategoryName() {
        return selectedPosition == RecyclerView.NO_POSITION ?
                null :
                categoryNames[selectedPosition];
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_category_button, parent, false);
        return new CategoryHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return categoryNames.length;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        CategoryName categoryName = categoryNames[position];
        holder.categoryButton.setText(categoryName.toString());
        if (selectedPosition == position) {
            int fireOpal = getColor(R.color.fire_opal);
            holder.categoryButton.setBackgroundColor(fireOpal);
            holder.categoryButton.setTextColor(Color.WHITE);
        } else {
            int lightBlue = getColor(R.color.light_blue);
            holder.categoryButton.setBackgroundColor(lightBlue);
            holder.categoryButton.setTextColor(Color.BLACK);
        }
    }

    private int getColor(int colorID) {
        return ContextCompat.getColor(
                SelectableCategoryAdapter.this.context, colorID
        );
    }

    class CategoryHolder extends RecyclerView.ViewHolder {
        final MaterialButton categoryButton;

        CategoryHolder(@NonNull View itemView) {
            super(itemView);
            categoryButton = itemView.findViewById(R.id.buttonCategory);
            categoryButton.setOnClickListener(view -> {
                selectedPosition = getLayoutPosition();
                notifyDataSetChanged();
            });
        }
    }
}
