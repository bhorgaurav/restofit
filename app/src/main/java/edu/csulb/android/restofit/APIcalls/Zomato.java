package edu.csulb.android.retrofit1;

import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

/**
 * Created by Administrator on 29-03-2017.
 */

public interface Zomato {

    @Headers({"Accept: application/json", "user-key: 42e3e7dfb9ef7fd7e388f824395a3b90"})
    @GET("api/v2.1/categories")
    Call<ResponseBody> getCategories();

    @Headers({"Accept: application/json", "user-key: 42e3e7dfb9ef7fd7e388f824395a3b90"})
    @GET("api/v2.1/cities")
    Call<ResponseBody> getCities();

    @Headers({"Accept: application/json", "user-key: 42e3e7dfb9ef7fd7e388f824395a3b90"})
    @GET("api/v2.1/locations?query=long%20%20beach")
    Call<ResponseBody> getLocation();

    @Headers({"Accept: application/json", "user-key: 42e3e7dfb9ef7fd7e388f824395a3b90"})
    @GET("api/v2.1/dailymenu?res_id=1024")
    Call<ResponseBody> getDailyMenu();

    @Headers({"Accept: application/json", "user-key: 42e3e7dfb9ef7fd7e388f824395a3b90"})
    @GET("https://developers.zomato.com/api/v2.1/restaurant?res_id=10924")
    Call<ResponseBody> getRestaurant();

    @Headers({"Accept: application/json", "user-key: 42e3e7dfb9ef7fd7e388f824395a3b90"})
    @GET("api/v2.1/reviews?res_id=10924")
    Call<ResponseBody> getReviews();

    @Headers({"Accept: application/json", "user-key: 42e3e7dfb9ef7fd7e388f824395a3b90"})
    @GET("api/v2.1/search")
    Call<ResponseBody> getSearchResults();

}
