package com.example.ruangberita.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ruangberita.R;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private List<String> categories;
    private List<String> categoryNames;
    private OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public interface OnCategoryClickListener {
        void onCategoryClick(String category, int position);
    }

    public CategoryAdapter(Context context, List<String> categories, List<String> categoryNames, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.categoryNames = categoryNames;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String categoryName = categoryNames.get(position);
        holder.textCategory.setText(categoryName);

        // Set selected state
        holder.textCategory.setSelected(position == selectedPosition);

        if (position == selectedPosition) {
            holder.textCategory.setBackgroundResource(R.drawable.category_selected_background);
            holder.textCategory.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.textCategory.setBackgroundResource(R.drawable.category_unselected_background);
            holder.textCategory.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            int oldPosition = selectedPosition;
            selectedPosition = adapterPosition;
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onCategoryClick(categories.get(adapterPosition), adapterPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryNames.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.text_category);
        }
    }
}