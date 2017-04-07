package edu.csulb.android.restofit.api;

import java.util.Map;

import edu.csulb.android.restofit.pojos.TokenResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface YelpAPI {

    String URL = "https://api.yelp.com/";
    String CLIENT_ID = "mt_yIgqhYaORjK5zBcuwhQ";
    String CLIENT_SECRET = "r0pelZ8c8b4b01qEAAJHwd3PPdZOzMHohukDmFX2GKlLJbgQMYkpPdakkW4Ym6E7";
    String GRANT_TYPE = "client_credentials";

    @FormUrlEncoded
    @POST("oauth2/token")
    Call<TokenResponse> getTokenAccess(@Field("client_id") String client_id, @Field("client_secret") String client_secret, @Field("grant_type") String grant_type);

    @GET("v3/businesses/search")
    Call<ResponseBody> search(@QueryMap(encoded = true) Map<String, String> parameters);
}