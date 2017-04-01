package edu.csulb.android.restofit.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String API_URL, boolean isZomato) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder().addInterceptor(interceptor);

        if (isZomato) {
            client.addInterceptor(new Interceptor() {
                /*
                * Credits: https://futurestud.io/tutorials/retrofit-add-custom-request-header
                * */
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder().addHeader("user-key", "42e3e7dfb9ef7fd7e388f824395a3b90")
                            .addHeader("Accept", "application/json")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });
        } else {
            // Add user token
        }

        retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();

        return retrofit;
    }

}