package edu.csulb.android.restofit.activities;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONObject;

import java.util.List;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.adapters.PagerAdapter;
import edu.csulb.android.restofit.api.APIClient;
import edu.csulb.android.restofit.api.LocationResponse;
import edu.csulb.android.restofit.api.ZomatoAPI;
import edu.csulb.android.restofit.pojos.TokenResponse;
import edu.csulb.android.restofit.api.YelpAPI;
import edu.csulb.android.restofit.obseravables.FilterManager;
import edu.csulb.android.restofit.utils.PreferenceHelper;
import edu.csulb.android.restofit.utils.PreferenceKeys;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import edu.csulb.android.restofit.obseravables.LocationHelper;

/*
* Uses https://github.com/Karumi/Dexter for checking and requesting permissions
**/
public class MainActivity extends SuperActivity {

    private TabLayout tabLayout;
    private MenuItem search;
    private FilterManager filterManager;
    LocationManager locationManager;
    LocationListener locationListener;
    public Double lat,lng;
    Button button;
    LocationHelper locationHelper;
    String entity_type;
    int entity_id;
    LocationResponse locationResponse=new LocationResponse();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceHelper.init(getApplicationContext());
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

    public void getLocation1(View view) {


        locationHelper = new LocationHelper();
        APIClient.getClient(ZomatoAPI.URL, true).create(ZomatoAPI.class).getLocation(33.7999444, -118.22615).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        JSONObject LocationData = new JSONObject(response.body().string());

                        System.out.print(LocationData);
                        entity_id=locationResponse.getEntityId();
                        entity_type=locationResponse.getEntityType();
                        getLocationDetails();
                        Log.e("3", "onResponse");

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
    void getLocationDetails(){

        APIClient.getClient(ZomatoAPI.URL, true).create(ZomatoAPI.class).getLocationDetails(entity_id, entity_type).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        JSONObject LocationDetails = new JSONObject(response.body().string());

                        System.out.print(LocationDetails);

                        Log.e("3", "onResponse");

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


}