package edu.csulb.android.restofit.viewmodels;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.view.View;

import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.pojos.Category;
import edu.csulb.android.restofit.views.activities.RestaurantResultsActivity;

public class CategoryItemViewModel extends BaseObservable {

    private Category mCategory;

    public CategoryItemViewModel(Category category) {
        this.mCategory = category;
    }

    public String getName() {
        return mCategory.getName();
    }

    public View.OnClickListener onCategoryNameClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), RestaurantResultsActivity.class);
                i.putExtra(StaticMembers.IntentFlags.CATEGORY, mCategory);
                v.getContext().startActivity(i);
            }
        };
    }
}