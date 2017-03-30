package edu.csulb.android.restofit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Observable;
import java.util.Observer;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.obseravables.FilterManager;

public class NearYouFragment extends SuperFragment implements Observer {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_near_you, container, false);
        return view;
    }

    @Override
    public void update(Observable observable, Object o) {
        String query = ((FilterManager) observable).getQuery();
//        adapter.filter(query);
    }
}
