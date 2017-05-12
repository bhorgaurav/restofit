package edu.csulb.android.restofit.views.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.adapters.PagerAdapter;
import edu.csulb.android.restofit.databinding.ActivityMainBinding;
import edu.csulb.android.restofit.helpers.AwarenessHelper;
import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.receivers.FenceBroadcastReceiver;
import edu.csulb.android.restofit.viewmodels.MainViewModel;

/*
* Uses https://github.com/Karumi/Dexter for checking and requesting permissions
**/
public class MainActivity extends SuperActivity {

    private MenuItem mMenuItemSearch;
    //    private FilterManager mFilterManager;
    private FenceBroadcastReceiver mFenceReceiver;
    private MainViewModel viewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);

        viewModel = new MainViewModel(getApplicationContext());
        viewModel.initYelp();

        mFenceReceiver = new FenceBroadcastReceiver();

        askPermissions();

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Near You"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Categories"));
        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

//        mFilterManager = new FilterManager();

        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), binding.tabLayout.getTabCount(), null);
        binding.pager.setAdapter(adapter);
        binding.pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        binding.tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    mMenuItemSearch.collapseActionView();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                binding.tabLayout.setVisibility(View.VISIBLE);
                binding.pager.setCurrentItem(tab.getPosition());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        // To show/hide the tabLayout when searching
        mMenuItemSearch = menu.findItem(R.id.search);
        mMenuItemSearch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                return false;
            }
        });

//        // To manage the searchView text
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) mMenuItemSearch.getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                mFilterManager.setQuery(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                mFilterManager.setQuery(newText);
//                return false;
//            }
//        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                viewModel.logout();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AwarenessHelper.registerFences(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {

            }

            @Override
            public void onFailure(@NonNull Status status) {

            }
        });
        registerReceiver(mFenceReceiver, new IntentFilter(StaticMembers.IntentFlags.FENCE_RECEIVER_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        AwarenessHelper.unregisterFences(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {

            }

            @Override
            public void onFailure(@NonNull Status status) {

            }
        });
        unregisterReceiver(mFenceReceiver);
    }
}