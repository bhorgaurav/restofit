package edu.csulb.android.restofit.views.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.MenuItem;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.databinding.ActivityRestaurantBinding;
import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.pojos.Restaurant;
import edu.csulb.android.restofit.viewmodels.RestaurantActivityViewModel;

public class RestaurantActivity extends SuperActivity {

    private RestaurantActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRestaurantBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant);

        Restaurant mRestaurant = (Restaurant) getIntent().getSerializableExtra(StaticMembers.IntentFlags.RESTAURANT);
        mViewModel = new RestaurantActivityViewModel(getApplicationContext(), mRestaurant);
        binding.setRestaurant(mViewModel);
        mViewModel.getRestaurantDetails();

        binding.collapsingToolbar.setTitle(mRestaurant.name);
        binding.collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}