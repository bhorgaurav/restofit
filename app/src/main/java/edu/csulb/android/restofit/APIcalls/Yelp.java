package edu.csulb.android.retrofit1;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Administrator on 30-03-2017.
 */

public interface Yelp {

  //@GET("https://api.yelp.com/v3/")
  //Call<ResponseBody> getAutoComplete();


  @POST("oauth2/token")
  Call<TokenResponse> getTokenAccess(@Body TokenRequest tokenRequest);
}
