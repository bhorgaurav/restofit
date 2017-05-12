package edu.csulb.android.restofit.api;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface MeaningCloud {

    String URL = "http://api.meaningcloud.com/";

    @Headers("content-type: application/x-www-form-urlencoded")
    @POST("sentiment-2.1")
    Call<ResponseBody> analyze(@QueryMap(encoded = true) Map<String, String> parameters);
}