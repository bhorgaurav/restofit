package edu.csulb.android.restofit.views.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.adapters.ReviewAdapter;
import edu.csulb.android.restofit.databinding.ActivityRestaurantBinding;
import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.pojos.Restaurant;
import edu.csulb.android.restofit.pojos.Review;
import edu.csulb.android.restofit.viewmodels.RestaurantActivityViewModel;

public class RestaurantActivity extends SuperActivity {

    private RestaurantActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRestaurantBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant);

        Restaurant mRestaurant = (Restaurant) getIntent().getSerializableExtra(StaticMembers.IntentFlags.RESTAURANT);

        try {
            Picasso.with(getApplicationContext()).load(mRestaurant.imageLink).into(binding.imageViewHeader);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ReviewAdapter adapter = new ReviewAdapter(getApplicationContext(), new ArrayList<Review>());
        mViewModel = new RestaurantActivityViewModel(mRestaurant, adapter, binding.listViewReviews);
        mViewModel.getRestaurantDetails();

        binding.setModel(mViewModel);
        binding.listViewReviews.setAdapter(adapter);
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