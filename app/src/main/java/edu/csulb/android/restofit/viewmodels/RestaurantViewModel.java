package edu.csulb.android.restofit.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.location.Address;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.csulb.android.restofit.adapters.RestaurantAdapter;
import edu.csulb.android.restofit.api.APIClient;
import edu.csulb.android.restofit.api.ZomatoAPI;
import edu.csulb.android.restofit.helpers.LocationHelper;
import edu.csulb.android.restofit.helpers.PreferenceHelper;
import edu.csulb.android.restofit.helpers.PreferenceKeys;
import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.pojos.Category;
import edu.csulb.android.restofit.pojos.Restaurant;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantViewModel extends BaseObservable {

    private RestaurantAdapter mAdapter;
    private Context mContext;

    public RestaurantViewModel(RestaurantAdapter adapter, Context context) {
        this.mAdapter = adapter;
        mContext = context;
    }

    public void populateList() {

        try {
            if (PreferenceHelper.contains(PreferenceKeys.NEAR_YOU)) {
                mAdapter.clear();
                JSONArray bestRestaurants = new JSONArray(PreferenceHelper.getString(PreferenceKeys.NEAR_YOU));
                mAdapter.clear();
                List<Restaurant> newList = parse(bestRestaurants);
                if (newList != null) {
                    mAdapter.addAll(newList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocationHelper.getAddress(mContext, new edu.csulb.android.restofit.interfaces.Callback() {

            @Override
            public void success(Object object) {

                Address address = (Address) object;
                APIClient.getClient(ZomatoAPI.URL, APIClient.CODE_ZOMATO).create(ZomatoAPI.class).getLocation(address.getAddressLine(1), address.getLatitude(), address.getLongitude())
                        .enqueue(new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    if (response.isSuccessful()) {
                                        JSONObject json = new JSONObject(response.body().string()).getJSONArray("location_suggestions").getJSONObject(0);

                                        APIClient.getClient(ZomatoAPI.URL, APIClient.CODE_ZOMATO).create(ZomatoAPI.class).getLocationDetails(json.getInt("entity_id"), json.getString("entity_type"))
                                                .enqueue(new Callback<ResponseBody>() {

                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        try {
                                                            JSONArray bestRestaurants = new JSONObject(response.body().string()).getJSONArray("best_rated_restaurant");
                                                            PreferenceHelper.save(PreferenceKeys.NEAR_YOU, bestRestaurants.toString());

                                                            mAdapter.clear();
                                                            List<Restaurant> newList = parse(bestRestaurants);
                                                            if (newList != null) {
                                                                mAdapter.addAll(newList);
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
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                            }
                        });
            }

            @Override
            public void fail(Object object) {
            }
        });
    }

    public void handleIntent(Intent intent) {
        if (intent.hasExtra(StaticMembers.IntentFlags.CATEGORY)) {
            // Opened from categories
            Category category = (Category) intent.getSerializableExtra(StaticMembers.IntentFlags.CATEGORY);

            Map<String, String> parameters = new TreeMap<>();
            parameters.put("lat", String.valueOf(LocationHelper.getLatitude()));
            parameters.put("lon", String.valueOf(LocationHelper.getLongitude()));
            parameters.put("category", category.name);
            parameters.put("radius", "3000");
            populateList(parameters);
        } else {
            // Opened from search
            Map<String, String> parameters = new TreeMap<>();

            Bundle bundle = intent.getExtras();
            Set<String> keys = bundle.keySet();
            for (String key : keys) {
                parameters.put(key, bundle.getString(key));
            }
            populateList(parameters);
        }
    }

    private void populateList(Map<String, String> parameters) {

        APIClient.getClient(ZomatoAPI.URL, APIClient.CODE_ZOMATO).create(ZomatoAPI.class).getSearchResults(parameters).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        JSONArray array = new JSONObject(response.body().string()).getJSONArray("restaurants");
                        mAdapter.clear();
                        List<Restaurant> newList = parse(array);
                        if (newList != null) {
                            mAdapter.addAll(newList);
                        }
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

    private List<Restaurant> parse(JSONArray array) {
        try {
            List<Restaurant> restaurants = new ArrayList<>(array.length());
            for (int i = 0; i < array.length(); i++) {
                JSONObject restaurant = array.getJSONObject(i).getJSONObject("restaurant");
                Restaurant r = new Restaurant();
                r.id = restaurant.getInt("id");
                r.name = restaurant.getString("name");
                r.imageLink = restaurant.getString("featured_image");
                r.average_cost = restaurant.getInt("average_cost_for_two");
                r.url = restaurant.getString("url");
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
            return restaurants;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void filter(String query) {
        mAdapter.filter(query);
    }
}