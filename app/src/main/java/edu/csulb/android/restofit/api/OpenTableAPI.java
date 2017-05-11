package edu.csulb.android.restofit.api;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface OpenTableAPI {

    String URL = "https://opentable.herokuapp.com/api/";

    @GET("restaurants")
    Call<ResponseBody> search(@QueryMap(encoded = true) Map<String, String> parameters);
}