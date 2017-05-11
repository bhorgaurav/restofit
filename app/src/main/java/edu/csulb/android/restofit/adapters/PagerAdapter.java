package edu.csulb.android.restofit.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import edu.csulb.android.restofit.obseravables.FilterManager;
import edu.csulb.android.restofit.views.fragments.CategoriesFragment;
import edu.csulb.android.restofit.views.fragments.NearYouFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;
//    private FilterManager mFilterManager;

    public PagerAdapter(FragmentManager manager, int mNumOfTabs, FilterManager mFilterManager) {
        super(manager);
        this.mNumOfTabs = mNumOfTabs;
//        this.mFilterManager = mFilterManager;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                NearYouFragment tab1 = new NearYouFragment();
//                mFilterManager.addObserver(tab1);
                return tab1;
            case 1:
                CategoriesFragment tab2 = new CategoriesFragment();
//                mFilterManager.addObserver(tab2);
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}