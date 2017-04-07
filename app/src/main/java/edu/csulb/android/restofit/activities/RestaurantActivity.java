package edu.csulb.android.restofit.activities;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Observer;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.databinding.ActivityRestaurantBinding;
import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.obseravables.FilterManager;
import edu.csulb.android.restofit.pojos.Restaurant;

public class RestaurantActivity extends SuperActivity {

    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRestaurantBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant);
        restaurant = (Restaurant) getIntent().getSerializableExtra(StaticMembers.IntentFlags.RESTAURANT);
        binding.setRestaurant(restaurant);
    }
}