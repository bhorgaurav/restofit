package edu.csulb.android.restofit.api;

import java.io.IOException;

import edu.csulb.android.restofit.helpers.PreferenceHelper;
import edu.csulb.android.restofit.helpers.PreferenceKeys;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static Retrofit retrofit = null;
    private static OkHttpClient client;

    public static final int CODE_ZOMATO = 1;
    public static final int CODE_YELP = 2;
    public static final int CODE_OPENTABLE = 3;

    public static Retrofit getClient(String API_URL, final int apiCode) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        /*
        * Credits: https://futurestud.io/tutorials/retrofit-add-custom-request-header
        **/
        client = new OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(new Interceptor() {
            /*
            * Credits: https://futurestud.io/tutorials/retrofit-add-custom-request-header
            **/
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();

                switch (apiCode) {
                    case CODE_ZOMATO:
                        requestBuilder.addHeader("user-key", ZomatoAPI.USER_KEY)
                                .addHeader("Accept", "application/json");
                        break;
                    case CODE_YELP:
                        requestBuilder.addHeader("Authorization", "Bearer " + PreferenceHelper.getString(PreferenceKeys.YELP_TOKEN));
                        break;
                    case CODE_OPENTABLE:
                        break;
                }

                requestBuilder.method(original.method(), original.body());
                return chain.proceed(requestBuilder.build());
            }
        }).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    public static void cancelAll() {
        client.dispatcher().cancelAll();
    }

}