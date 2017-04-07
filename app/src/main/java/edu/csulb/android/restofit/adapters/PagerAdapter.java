package edu.csulb.android.restofit.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import edu.csulb.android.restofit.fragments.CategoriesFragment;
import edu.csulb.android.restofit.fragments.NearYouFragment;
import edu.csulb.android.restofit.obseravables.FilterManager;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;
    private FilterManager filterManager;

    public PagerAdapter(FragmentManager manager, int numOfTabs, FilterManager filterManager) {
        super(manager);
        this.numOfTabs = numOfTabs;
        this.filterManager = filterManager;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                NearYouFragment tab1 = new NearYouFragment();
                return tab1;
            case 1:
                CategoriesFragment tab2 = new CategoriesFragment();
                filterManager.addObserver(tab2);
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}