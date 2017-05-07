package edu.csulb.android.restofit.views.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

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

    private Restaurant mRestaurant;
    private ImageView mImageViewFeatured;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRestaurantBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant);
        mRestaurant = (Restaurant) getIntent().getSerializableExtra(StaticMembers.IntentFlags.RESTAURANT);
        binding.setRestaurant(mRestaurant);
        binding.setHandler(this);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle(mRestaurant.name);
        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        mImageViewFeatured = (ImageView) findViewById(R.id.image_view_header);

        Map<String, String> parameters = new TreeMap<>();
        parameters.put("term", mRestaurant.name);
        parameters.put("location", mRestaurant.address);
        parameters.put("radius", "1000");
        parameters.put("limit", "1");
        parameters.put("latitude", String.valueOf(mRestaurant.latitude));
        parameters.put("longitude", String.valueOf(mRestaurant.longitude));
        APIClient.getClient(YelpAPI.URL, false).create(YelpAPI.class).search(parameters).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string()).getJSONArray("businesses").getJSONObject(0);
                        if (jsonObject != null) {
                            mRestaurant.imageLink = jsonObject.getString("image_url");
                            mRestaurant.phone = jsonObject.getString("phone");
                            Picasso.with(getApplicationContext()).load(mRestaurant.imageLink).into(mImageViewFeatured);
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
                if (mRestaurant.phone != null) {
                    Uri number = Uri.parse("tel:" + mRestaurant.phone);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                } else {
                    Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_show_map:
                String geoCode = "geo:<" + mRestaurant.latitude + ">,<" + mRestaurant.longitude + ">&z=16?q=" + mRestaurant.name;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoCode));
                startActivity(intent);
                break;
            case R.id.button_booking:
                Toast.makeText(this, "Booking to be added.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}