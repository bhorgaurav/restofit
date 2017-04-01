package edu.csulb.android.restofit.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ZomatoAPI {

    String URL = "https://developers.zomato.com/";

    @GET("api/v2.1/categories")
    Call<ResponseBody> getCategories();

    @GET("api/v2.1/cities")
    Call<ResponseBody> getCities();

    @GET("api/v2.1/locations?query=long%20%20beach")
    Call<ResponseBody> getLocation();

    @GET("api/v2.1/dailymenu?res_id=1024")
    Call<ResponseBody> getDailyMenu();

    @GET("https://developers.zomato.com/api/v2.1/restaurant?res_id=10924")
    Call<ResponseBody> getRestaurant();

    @GET("api/v2.1/reviews?res_id=10924")
    Call<ResponseBody> getReviews();

    @GET("api/v2.1/search")
    Call<ResponseBody> getSearchResults();

}