package edu.csulb.android.restofit.activities;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;
import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.adapters.PagerAdapter;
import edu.csulb.android.restofit.api.APIClient;
import edu.csulb.android.restofit.api.YelpAPI;
import edu.csulb.android.restofit.helpers.AwarenessHelper;
import edu.csulb.android.restofit.helpers.LocationHelper;
import edu.csulb.android.restofit.helpers.PreferenceHelper;
import edu.csulb.android.restofit.helpers.PreferenceKeys;
import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.obseravables.FilterManager;
import edu.csulb.android.restofit.pojos.TokenResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
* Uses https://github.com/Karumi/Dexter for checking and requesting permissions
**/
public class MainActivity extends SuperActivity {

    private PendingIntent mFencePendingIntent;
    private TabLayout tabLayout;
    private MenuItem search;
    private FilterManager filterManager;
    private GoogleApiClient mGoogleApiClient;
    private HeadphoneFenceBroadcastReceiver fenceReceiver;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceHelper.init(getApplicationContext());
        LocationHelper.init(getApplicationContext());
        AwarenessHelper.init(getApplicationContext());

        fenceReceiver = new HeadphoneFenceBroadcastReceiver();
        Intent intent = new Intent(StaticMembers.IntentFlags.FENCE_RECEIVER_ACTION);
        mFencePendingIntent = PendingIntent.getBroadcast(MainActivity.this, 10001, intent, 0);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();

        initYelp();

        askPermissions();

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Near You"));
        tabLayout.addTab(tabLayout.newTab().setText("Categories"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        filterManager = new FilterManager();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), filterManager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                search.collapseActionView();
                tabLayout.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void askPermissions() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (!report.areAllPermissionsGranted()) {
                    Toast.makeText(getApplicationContext(), "The app needs access to your location to work. Please update from settings.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void initYelp() {
        // Ensure that, you have the token. If not, request and save a new token.
        if (!PreferenceHelper.contains(PreferenceKeys.YELP_TOKEN)) {
            APIClient.getClient(YelpAPI.URL, false).create(YelpAPI.class).getTokenAccess(YelpAPI.CLIENT_ID, YelpAPI.CLIENT_SECRET, YelpAPI.GRANT_TYPE)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // To show/hide the tabLayout when searching
        search = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(search, new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                tabLayout.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                tabLayout.setVisibility(View.GONE);
                return true;
            }
        });

        // To manage the searchView text
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                filterManager.setQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterManager.setQuery(newText);
                return false;
            }
        });

        return true;
    }

    private class HeadphoneFenceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            FenceState fenceState = FenceState.extract(intent);

            Log.d(TAG, "Fence Receiver Received");

            if (TextUtils.equals(fenceState.getFenceKey(), StaticMembers.FenceKeys.HEADPHONES_RUNNING_OR_PLUGGED_IN)) {
                switch (fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Log.i(TAG, "Fence > Headphones are plugged in.");
                        Toast.makeText(context, "Fence > " + StaticMembers.FenceKeys.HEADPHONES_RUNNING_OR_PLUGGED_IN, Toast.LENGTH_SHORT).show();

                        Location l = LocationHelper.getLastKnownLocation(context);

                        Bundle bundle = new Bundle();
                        bundle.putString("q", "robeks jamba");
                        bundle.putString("radius", "10000");
                        bundle.putString("lat", String.valueOf(l.getLatitude()));
                        bundle.putString("lon", String.valueOf(l.getLongitude()));

                        PugNotification.with(context)
                                .load()
                                .title("Going for a run?")
                                .message("Robecks and Jamba Juice near you.")
                                .bigTextStyle("Robecks and Jamba Juice near you.")
                                .smallIcon(R.drawable.pugnotification_ic_launcher)
                                .largeIcon(R.drawable.pugnotification_ic_launcher)
                                .flags(Notification.DEFAULT_ALL)
                                .click(RestaurantResultsActivity.class, bundle)
                                .simple()
                                .build();
                        break;
                    case FenceState.FALSE:
                        Log.i(TAG, "Fence > Headphones are NOT plugged in.");
                        break;
                    case FenceState.UNKNOWN:
                        Log.i(TAG, "Fence > The headphone fence is in an unknown state.");
                        break;
                }
            }
        }
    }

    private void registerFences() {
        AwarenessFence headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
        AwarenessFence runningFence = HeadphoneFence.during(DetectedActivityFence.RUNNING);
        AwarenessFence runningWithHeadphones = AwarenessFence.and(headphoneFence, runningFence);
        AwarenessFence headphoneRunningOrPluggedIn = AwarenessFence.or(runningWithHeadphones, headphoneFence);

        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .addFence(StaticMembers.FenceKeys.HEADPHONES_RUNNING_OR_PLUGGED_IN, headphoneRunningOrPluggedIn, mFencePendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        TAG = "TAG";
                        if (status.isSuccess()) {
                            Log.i(TAG, "Fence was successfully registered.");
                        } else {
                            Log.e(TAG, "Fence could not be registered: " + status);
                        }
                    }
                });
    }

    private void unregisterFences() {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(StaticMembers.FenceKeys.HEADPHONES_RUNNING_OR_PLUGGED_IN)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Log.i(TAG, "Fence " + "headphoneFenceKey" + " successfully removed.");
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.i(TAG, "Fence " + "headphoneFenceKey" + " could NOT be removed.");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerFences();
        registerReceiver(fenceReceiver, new IntentFilter(StaticMembers.IntentFlags.FENCE_RECEIVER_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterFences();
        unregisterReceiver(fenceReceiver);
    }
}