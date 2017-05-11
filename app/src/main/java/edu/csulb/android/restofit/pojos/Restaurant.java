package edu.csulb.android.restofit.pojos;

import java.io.Serializable;

public class Restaurant implements Serializable {

    public int id, zip, average_cost, is_delivering_now, has_online_delivery;
    public double longitude, latitude;
    public String name, address, aggregate_rating, currency, cuisines, rating_color, imageLink, phone, state, city, url;
}