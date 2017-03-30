package edu.csulb.android.restofit.activities;

import android.app.SearchManager;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.adapters.PagerAdapter;
import edu.csulb.android.restofit.obseravables.FilterManager;

public class MainActivity extends SuperActivity {

    private TabLayout tabLayout;
    private MenuItem search;
    private FilterManager filterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

}