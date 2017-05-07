package edu.csulb.android.restofit.views.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.adapters.RestaurantAdapter;
import edu.csulb.android.restofit.databinding.ActivityRestaurantResultsBinding;
import edu.csulb.android.restofit.pojos.Restaurant;
import edu.csulb.android.restofit.viewmodels.RestaurantViewModel;

public class RestaurantResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityRestaurantResultsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant_results);
        RestaurantAdapter adapter = new RestaurantAdapter(new ArrayList<Restaurant>());
        binding.restaurants.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.restaurants.setAdapter(adapter);

        RestaurantViewModel mViewModel = new RestaurantViewModel(adapter, getApplicationContext());
        mViewModel.handleIntent(getIntent());
    }
}