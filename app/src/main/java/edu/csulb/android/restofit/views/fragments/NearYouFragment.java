package edu.csulb.android.restofit.views.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import edu.csulb.android.restofit.adapters.RestaurantAdapter;
import edu.csulb.android.restofit.api.APIClient;
import edu.csulb.android.restofit.databinding.FragmentNearYouBinding;
import edu.csulb.android.restofit.helpers.LocationHelper;
import edu.csulb.android.restofit.obseravables.FilterManager;
import edu.csulb.android.restofit.pojos.Restaurant;
import edu.csulb.android.restofit.viewmodels.RestaurantViewModel;

public class NearYouFragment extends SuperFragment implements Observer{

    private RestaurantViewModel mViewModel;
    private AlertDialog alert;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentNearYouBinding binding = FragmentNearYouBinding.inflate(inflater, container, false);
        RestaurantAdapter adapter = new RestaurantAdapter(new ArrayList<Restaurant>());
        binding.restaurants.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.restaurants.setAdapter(adapter);
        mViewModel = new RestaurantViewModel(adapter, getContext());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("LocationHelper.statusCheck(): " + LocationHelper.statusCheck());
        if (!LocationHelper.statusCheck()) {
            buildAlertMessageNoGps();
        } else {
            mViewModel.populateList();
            if (alert != null) alert.dismiss();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        APIClient.cancelAll();
    }

    @Override
    public void update(Observable o, Object arg) {
        String query = ((FilterManager) o).getQuery();
        mViewModel.filter(query);
    }
}