package edu.csulb.android.restofit.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.activities.RestaurantResultsActivity;
import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.pojos.Category;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private List<Category> categories;
    private List<Category> filteredCategories = new ArrayList<>();

    public CategoriesAdapter(List<Category> categories) {
        this.categories = categories;
        this.filteredCategories.addAll(this.categories);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Category category = filteredCategories.get(position);
        holder.textViewCategoryName.setText(category.name);
        holder.textViewCategoryName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), RestaurantResultsActivity.class);
                i.putExtra(StaticMembers.IntentFlags.CATEGORY, category);
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredCategories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewCategoryName;

        ViewHolder(View view) {
            super(view);
            textViewCategoryName = (TextView) view.findViewById(R.id.text_view_category_name);
        }
    }

    public void filter(String text) {
        System.out.println("filter text: " + text);
        filteredCategories.clear();
        if (text.isEmpty()) {
            filteredCategories.addAll(categories);
        } else {
            text = text.toLowerCase();
            for (Category item : categories) {
                if (item.name.toLowerCase().contains(text)) {
                    filteredCategories.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
