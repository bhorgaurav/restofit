package edu.csulb.android.retrofit1;

import edu.csulb.android.retrofit1.Zomato;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 30-03-2017.
 */

public class APIClient {
    public static final String ZOMATO_API_URL = "";
    public static final String YELP_API_URL = "";

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String API_URL) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

}
