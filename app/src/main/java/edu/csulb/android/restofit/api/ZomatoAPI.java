package edu.csulb.android.restofit.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ZomatoAPI {

    String URL = "https://developers.zomato.com/";
    String USER_KEY = "42e3e7dfb9ef7fd7e388f824395a3b90";

    @GET("api/v2.1/categories")
    Call<ResponseBody> getCategories();

    @GET("api/v2.1/cities")
    Call<ResponseBody> getCities();

    @GET("api/v2.1/locations")
    Call<ResponseBody> getLocation(@Query("query") String query, @Query("latitude") double latitude, @Query("longitude") double longitude);

    @GET("api/v2.1/location_details")
    Call<ResponseBody> getLocationDetails(@Query("entity_id") int entity_id, @Query("entity_type") String entity_type);

    @GET("api/v2.1/dailymenu?res_id=1024")
    Call<ResponseBody> getDailyMenu();

    @GET("https://developers.zomato.com/api/v2.1/restaurant?res_id=10924")
    Call<ResponseBody> getRestaurant();

    @GET("api/v2.1/reviews?res_id=10924")
    Call<ResponseBody> getReviews();

    @GET("api/v2.1/search")
    Call<ResponseBody> getSearchResults();

}