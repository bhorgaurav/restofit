package edu.csulb.android.retrofit1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    String url = "https://developers.zomato.com/";
    Button b1,b2;
APIClient apiClient=new APIClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
b2=(Button) findViewById(R.id.location);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAccessToken();
                Log.e("5","location");
            }
        });
        b1 = (Button) findViewById(R.id.categories);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getZomatoCategories();
                Log.e("34", "hello");
            }
        });

    }


    void getZomatoCategories() {
        Retrofit retrofit=apiClient.getClient("https://developers.zomato.com/");
        Zomato service =retrofit.create(Zomato.class);



        Call<ResponseBody> call = service.getCategories();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        JSONObject CategoryData = new JSONObject(response.body().string());

                        System.out.print(CategoryData);
                        Log.e("3", "onRespone");
                    } else {
                        System.out.println(response.errorBody().string());
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    void getZomatoLocation() {
        Retrofit retrofit=apiClient.getClient("https://developers.zomato.com/");
        Zomato service =retrofit.create(Zomato.class);



        Call<ResponseBody> call = service.getLocation();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    if (response.isSuccessful()) {
                        JSONObject CategoryData = new JSONObject(response.body().string());

                        System.out.print(CategoryData);
                        Log.e("3", "onRespone location");
                    } else {
                        System.out.println(response.errorBody().string());
                    }
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    void getAccessToken()
    {
        TokenRequest tokenRequest=new TokenRequest();
        //tokenRequest.setGrant_type(mGrantView);
        tokenRequest.setClient_id("mt_yIgqhYaORjK5zBcuwhQ");
        tokenRequest.setClient_secret("r0pelZ8c8b4b01qEAAJHwd3PPdZOzMHohukDmFX2GKlLJbgQMYkpPdakkW4Ym6E7");
        Retrofit retrofit=apiClient.getClient("https://api.yelp.com/v3/");
        Yelp service =retrofit.create(Yelp.class);



        Call<TokenResponse> call = service.getTokenAccess(tokenRequest);

        call.enqueue(new Callback<TokenResponse>() {


            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                int statuscode=response.code();
                TokenResponse tokenResponse=response.body();
                Log.d("1","onResponse"+ statuscode);
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Log.d("2","onFailure"+t.getMessage());


            }
        });
    }
    }

