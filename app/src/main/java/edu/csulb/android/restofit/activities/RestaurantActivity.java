package edu.csulb.android.restofit.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.api.APIClient;
import edu.csulb.android.restofit.api.YelpAPI;
import edu.csulb.android.restofit.databinding.ActivityRestaurantBinding;
import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.pojos.Restaurant;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantActivity extends SuperActivity {

    private Restaurant restaurant;
    private ImageView imageView;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRestaurantBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant);
        restaurant = (Restaurant) getIntent().getSerializableExtra(StaticMembers.IntentFlags.RESTAURANT);
        binding.setRestaurant(restaurant);
        binding.setHandler(this);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(restaurant.name);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        imageView = (ImageView) findViewById(R.id.image_view_header);

        Map<String, String> parameters = new TreeMap<>();
        parameters.put("term", restaurant.name);
        parameters.put("location", restaurant.address);
        parameters.put("radius", "1000");
        parameters.put("limit", "1");
        parameters.put("latitude", String.valueOf(restaurant.latitude));
        parameters.put("longitude", String.valueOf(restaurant.longitude));
        APIClient.getClient(YelpAPI.URL, false).create(YelpAPI.class).search(parameters).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string()).getJSONArray("businesses").getJSONObject(0);
                        if (jsonObject != null) {
                            restaurant.imageLink = jsonObject.getString("image_url");
                            restaurant.phone = jsonObject.getString("phone");
                            Picasso.with(getApplicationContext()).load(restaurant.imageLink).into(imageView);
                        }
                    } else {
                        System.out.println(response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_call:
                if (restaurant.phone != null) {
                    Uri number = Uri.parse("tel:" + restaurant.phone);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                } else {
                    Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_show_map:
                String geoCode = "geo:<" + restaurant.latitude + ">,<" + restaurant.longitude + ">&z=16?q=" + restaurant.name;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoCode));
                startActivity(intent);
                break;
        }
    }
}