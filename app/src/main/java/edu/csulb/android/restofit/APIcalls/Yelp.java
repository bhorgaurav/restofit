package edu.csulb.android.retrofit1;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Administrator on 30-03-2017.
 */

public interface Yelp {
@Headers("Authorization: Bearer msjzKSeIuHoOCtz8rLc6FyoiQM4trLZWVWbNlNZbyvrmkKHNJqHMM5kFKISkxo2ffhJa7hY8rNNj1MpvfvoEitFhhRVFazKYbeNw2kyrRuuBL2r4601_3gmC5QLfWHYx")
@GET("businesses/four-barrel-coffee-san-francisco/reviews")
Call<ResponseBody> getReviews();

  @FormUrlEncoded
  @POST("oauth2/token")
  Call<TokenResponse> getTokenAccess(@Field("client_id") String client_id, @Field("client_secret") String client_secret, @Field("grant_type") String grant_type);
}
