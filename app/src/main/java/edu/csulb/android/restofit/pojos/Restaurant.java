package edu.csulb.android.restofit.pojos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Restaurant implements Serializable {

    public int id;
    public String rating_color, imageLink, phone;
    public int average_cost, is_delivering_now, has_online_delivery;
    public double longitude, latitude;
    public String name, address, aggregate_rating, currency, cuisines;

    public static List<Restaurant> parse(JSONArray array) {
        try {
            List<Restaurant> restaurants = new ArrayList<>(array.length());
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
            return restaurants;
        } catch (JSONException e) {
            return null;
        }

    }
}