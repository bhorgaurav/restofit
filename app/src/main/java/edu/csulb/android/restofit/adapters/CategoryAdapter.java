package edu.csulb.android.restofit.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.databinding.ItemCategoryBinding;
import edu.csulb.android.restofit.pojos.Category;
import edu.csulb.android.restofit.viewmodels.CategoryItemViewModel;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.BindingHolder> {

    private List<Category> mCategories;
    private List<Category> mFilteredCategories = new ArrayList<>();
    private Context mContext;

    public CategoryAdapter(List<Category> categories, Context context) {
        this.mCategories = categories;
        this.mContext = context;
        this.mFilteredCategories.addAll(this.mCategories);
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_category, parent, false);
        return new BindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        holder.binding.setCategory(new CategoryItemViewModel(mFilteredCategories.get(position)));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mFilteredCategories.size();
    }

    public void add(Category category) {
        this.mCategories.add(category);
        this.mFilteredCategories.add(category);
        notifyDataSetChanged();
    }

    public void clear() {
        int size = this.mFilteredCategories.size();
        this.mFilteredCategories.clear();
        this.mCategories.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class BindingHolder extends RecyclerView.ViewHolder {
        private ItemCategoryBinding binding;

        public BindingHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void filter(String text) {
        mFilteredCategories.clear();
        if (text.isEmpty()) {
            mFilteredCategories.addAll(mCategories);
        } else {
            text = text.toLowerCase();
            for (Category item : mCategories) {
                if (item.name.toLowerCase().contains(text)) {
                    mFilteredCategories.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
