package edu.csulb.android.restofit.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.adapters.RestaurantAdapter;
import edu.csulb.android.restofit.api.APIClient;
import edu.csulb.android.restofit.api.ZomatoAPI;
import edu.csulb.android.restofit.helpers.LocationHelper;
import edu.csulb.android.restofit.helpers.PreferenceHelper;
import edu.csulb.android.restofit.helpers.PreferenceKeys;
import edu.csulb.android.restofit.pojos.Restaurant;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearYouFragment extends SuperFragment {

    @BindView(R.id.recycler_view_restaurants)
    RecyclerView recyclerViewRestaurants;

    @BindView(R.id.progress_wheel)
    ProgressWheel progressWheel;

    private RestaurantAdapter restaurantAdapter;
    private List<Restaurant> restaurants = new ArrayList<>();
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_near_you, container, false);
        unbinder = ButterKnife.bind(this, view);
        ButterKnife.setDebug(true);

        recyclerViewRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));
        updateUI(true, null);
        return view;
    }

    private void populateList() {
        try {
            if (PreferenceHelper.contains(PreferenceKeys.NEAR_YOU)) {
                JSONArray bestRestaurants = new JSONArray(PreferenceHelper.getString(PreferenceKeys.NEAR_YOU));
                for (int i = 0; i < bestRestaurants.length(); i++) {
                    JSONObject restaurant = bestRestaurants.getJSONObject(i).getJSONObject("restaurant");
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
            }
            updateUI(false, null);
        } catch (Exception e) {
            e.printStackTrace();
            updateUI(false, e.getMessage());
        }

        LocationHelper.getAddress(getContext(), new edu.csulb.android.restofit.interfaces.Callback() {
            @Override
            public void success(Object object) {
                Address address = (Address) object;
                APIClient.getClient(ZomatoAPI.URL, true).create(ZomatoAPI.class).getLocation(address.getAddressLine(1), address.getLatitude(), address.getLongitude())
                        .enqueue(new Callback<ResponseBody>() {

                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    if (response.isSuccessful()) {
                                        JSONObject json = new JSONObject(response.body().string()).getJSONArray("location_suggestions").getJSONObject(0);

                                        APIClient.getClient(ZomatoAPI.URL, true).create(ZomatoAPI.class).getLocationDetails(json.getInt("entity_id"), json.getString("entity_type"))
                                                .enqueue(new Callback<ResponseBody>() {

                                                    @Override
                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                        try {
                                                            JSONArray bestRestaurants = new JSONObject(response.body().string()).getJSONArray("best_rated_restaurant");
                                                            PreferenceHelper.save(PreferenceKeys.NEAR_YOU, bestRestaurants.toString());
                                                            for (int i = 0; i < bestRestaurants.length(); i++) {
                                                                JSONObject restaurant = bestRestaurants.getJSONObject(i).getJSONObject("restaurant");
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

                                                            updateUI(false, null);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            updateUI(false, "Error fetching restaurants: " + e.getMessage());
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                        t.printStackTrace();
                                                        updateUI(false, "Error fetching restaurants: " + t.getMessage());
                                                    }
                                                });
                                    } else {
                                        updateUI(false, "Error fetching restaurants: " + response.errorBody().string());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    updateUI(false, "Error fetching restaurants: " + e.getMessage());
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                updateUI(false, "Error fetching restaurants: " + t.getMessage());
                            }
                        });
            }

            @Override
            public void fail(Object object) {
                updateUI(false, "Error fetching restaurants.");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!LocationHelper.statusCheck()) {
            buildAlertMessageNoGps();
        } else {
            updateUI(true, null);
            populateList();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        updateUI(false, "The application needs GPS to work correctly.");
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void updateUI(boolean show, String message) {
        if (progressWheel != null) {
            if (show) {
                progressWheel.spin();
                progressWheel.setVisibility(View.VISIBLE);
            } else {
                progressWheel.stopSpinning();
                progressWheel.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(message)) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        APIClient.cancelAll();
        unbinder.unbind();
    }
}