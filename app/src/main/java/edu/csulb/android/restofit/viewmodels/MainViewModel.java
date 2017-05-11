package edu.csulb.android.restofit.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import edu.csulb.android.restofit.api.APIClient;
import edu.csulb.android.restofit.api.YelpAPI;
import edu.csulb.android.restofit.helpers.AwarenessHelper;
import edu.csulb.android.restofit.helpers.LocationHelper;
import edu.csulb.android.restofit.helpers.PreferenceHelper;
import edu.csulb.android.restofit.helpers.PreferenceKeys;
import edu.csulb.android.restofit.pojos.TokenResponse;
import edu.csulb.android.restofit.views.activities.LoginActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends BaseObservable {

    private Context mContext;

    public MainViewModel(Context context) {
        this.mContext = context;

        PreferenceHelper.init(context);
        LocationHelper.init(context);
        AwarenessHelper.init(context);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public void initYelp() {
        // Ensure that, you have the token. If not, request and save a new token.
        if (!PreferenceHelper.contains(PreferenceKeys.YELP_TOKEN)) {
            APIClient.getClient(YelpAPI.URL, APIClient.CODE_YELP).create(YelpAPI.class).getTokenAccess(YelpAPI.CLIENT_ID, YelpAPI.CLIENT_SECRET, YelpAPI.GRANT_TYPE)
                    .enqueue(new Callback<TokenResponse>() {

                        @Override
                        public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                            try {
                                if (response.isSuccessful()) {
                                    PreferenceHelper.save(PreferenceKeys.YELP_TOKEN, response.body().getAccessToken());
                                } else {
                                    System.out.println(response.errorBody().string());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<TokenResponse> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
        }
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        mContext.startActivity(new Intent(mContext, LoginActivity.class));
    }
}
