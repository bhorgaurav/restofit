package edu.csulb.android.restofit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.adapters.RestaurantAdapter;
import edu.csulb.android.restofit.api.APIClient;
import edu.csulb.android.restofit.api.ZomatoAPI;
import edu.csulb.android.restofit.helpers.LocationHelper;
import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.pojos.Category;
import edu.csulb.android.restofit.pojos.Restaurant;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRestaurants;
    private RestaurantAdapter restaurantAdapter;
    private List<Restaurant> restaurants = new ArrayList<>();
    private ProgressWheel progressWheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_near_you);

        recyclerViewRestaurants = (RecyclerView) findViewById(R.id.recycler_view_restaurants);
        recyclerViewRestaurants.setLayoutManager(new LinearLayoutManager(this));
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        progressWheel.setVisibility(View.VISIBLE);
        progressWheel.spin();

        Intent intent = getIntent();
        if (intent.hasExtra(StaticMembers.IntentFlags.CATEGORY)) {
            // Opened from categories
            Category category = (Category) intent.getSerializableExtra(StaticMembers.IntentFlags.CATEGORY);

            Map<String, String> parameters = new TreeMap<>();
            parameters.put("lat", String.valueOf(LocationHelper.location.getLatitude()));
            parameters.put("lon", String.valueOf(LocationHelper.location.getLongitude()));
            parameters.put("category", category.name);
            parameters.put("radius", "3000");

            APIClient.getClient(ZomatoAPI.URL, true).create(ZomatoAPI.class).getSearchResults(parameters).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful()) {
                            JSONArray array = new JSONObject(response.body().string()).getJSONArray("restaurants");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject restaurant = array.getJSONObject(i).getJSONObject("restaurant");
                                Restaurant r = new Restaurant();
                                r.id = restaurant.getInt("id");
                                r.name = restaurant.getString("name");
                                r.average_cost = restaurant.getInt("average_cost_for_two");
                                r.aggregate_rating = restaurant.getJSONObject("user_rating").getString("aggregate_rating");
                                r.rating_color = restaurant.getJSONObject("user_rating").getString("rating_color");
                                r.address = restaurant.getJSONObject("location").getString("address");
                                r.cuisines = restaurant.getString("cuisines");
                                r.currency = restaurant.getString("currency");
                                r.longitude = restaurant.getJSONObject("location").getDouble("longitude");
                                r.latitude = restaurant.getJSONObject("location").getDouble("latitude");
                                r.is_delivering_now = restaurant.getInt("is_delivering_now");
                                r.has_online_delivery = restaurant.getInt("has_online_delivery");
                                restaurants.add(r);
                            }
                            restaurantAdapter = new RestaurantAdapter(restaurants);
                            recyclerViewRestaurants.setAdapter(restaurantAdapter);

                            progressWheel.stopSpinning();
                            progressWheel.setVisibility(View.GONE);
                        } else {
                            progressWheel.stopSpinning();
                            progressWheel.setVisibility(View.GONE);
                            Toast.makeText(RestaurantResultsActivity.this, "Error fetching restaurants: " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressWheel.stopSpinning();
                        progressWheel.setVisibility(View.GONE);
                        Toast.makeText(RestaurantResultsActivity.this, "Error fetching restaurants: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    progressWheel.stopSpinning();
                    progressWheel.setVisibility(View.GONE);
                    Toast.makeText(RestaurantResultsActivity.this, "Error fetching restaurants: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Opened from search
        }
    }
}