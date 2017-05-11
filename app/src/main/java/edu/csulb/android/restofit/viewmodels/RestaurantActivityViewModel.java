package edu.csulb.android.restofit.viewmodels;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.api.APIClient;
import edu.csulb.android.restofit.api.YelpAPI;
import edu.csulb.android.restofit.pojos.Restaurant;
import edu.csulb.android.restofit.views.activities.AddReviewActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantActivityViewModel extends BaseObservable {

    private Restaurant mRestaurant;
    private ObservableField<String> imageLink = new ObservableField<>();

    public RestaurantActivityViewModel(Restaurant restaurant) {
        this.mRestaurant = restaurant;
        this.imageLink.set(mRestaurant.imageLink);
    }

    public String getName() {
        return mRestaurant.name;
    }

    public String getAggregate_rating() {
        return mRestaurant.aggregate_rating;
    }

    public String getAverage_cost() {
        return String.valueOf(mRestaurant.average_cost);
    }

    public String getIs_delivering_now() {
        if (mRestaurant.is_delivering_now == 1) {
            return "Yes";
        } else {
            return "No";
        }
    }

    public String getAddress() {
        return mRestaurant.address;
    }

    @Bindable
    public String getImageLink() {
        return imageLink.get();
    }

    @BindingAdapter({"bind:imageLink"})
    public static void loadImage(ImageView imageView, String imageLink) {
        Picasso.with(imageView.getContext()).load(imageLink).into(imageView);
    }

    public void getRestaurantDetails() {

        final Map<String, String> parameters = new TreeMap<>();
        parameters.put("term", mRestaurant.name);
        parameters.put("location", mRestaurant.address);
        parameters.put("radius", "1000");
        parameters.put("limit", "1");
        parameters.put("latitude", String.valueOf(mRestaurant.latitude));
        parameters.put("longitude", String.valueOf(mRestaurant.longitude));
        APIClient.getClient(YelpAPI.URL, APIClient.CODE_YELP).create(YelpAPI.class).search(parameters).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string()).getJSONArray("businesses").getJSONObject(0);
                        if (jsonObject != null) {
                            mRestaurant.imageLink = jsonObject.getString("image_url");
                            mRestaurant.phone = jsonObject.getString("phone");
                            System.out.println("location: " + jsonObject.getJSONObject("location"));
                            mRestaurant.state = jsonObject.getJSONObject("location").getString("state");
                            mRestaurant.city = jsonObject.getJSONObject("location").getString("city");
                            mRestaurant.zip = jsonObject.getJSONObject("location").getInt("zip_code");

//                            parameters.clear();
//                            parameters.put("name", mRestaurant.name);
//                            parameters.put("state", mRestaurant.state);
//                            parameters.put("city", mRestaurant.city);
//                            APIClient.getClient(OpenTableAPI.URL, APIClient.CODE_OPENTABLE).create(OpenTableAPI.class).search(parameters).enqueue(new Callback<ResponseBody>() {
//                                @Override
//                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                    try {
//                                        if (response.isSuccessful()) {
//                                            System.out.println("response.body(): " + response.body().string());
//                                        } else {
//                                            System.out.println(response.errorBody().string());
//                                        }
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                    t.printStackTrace();
//                                }
//                            });
                        }
                    } else {
                        System.out.println(response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_call:
                if (!TextUtils.isEmpty(mRestaurant.phone)) {
                    Uri number = Uri.parse("tel:" + mRestaurant.phone);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    view.getContext().startActivity(callIntent);
                }
                break;
            case R.id.button_booking:
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mRestaurant.url)));
                break;
            case R.id.button_show_map:
                String geoCode = "geo:<" + mRestaurant.latitude + ">,<" + mRestaurant.longitude + ">&z=16?q=" + mRestaurant.name;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoCode));
                view.getContext().startActivity(intent);
                break;
            case R.id.button_add_review:
                view.getContext().startActivity(new Intent(view.getContext(), AddReviewActivity.class));
                break;
        }
    }
}