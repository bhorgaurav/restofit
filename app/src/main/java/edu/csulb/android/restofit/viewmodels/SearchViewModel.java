package edu.csulb.android.restofit.viewmodels;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import edu.csulb.android.restofit.views.activities.RestaurantResultsActivity;

public class SearchViewModel extends BaseObservable {

    private Context context;
    String state, city, category;

    public SearchViewModel(Context context) {
        this.context = context;
    }

    public TextWatcher stateWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            state = s.toString();
        }
    };

    public TextWatcher cityWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            city = s.toString();
        }
    };

    public TextWatcher categoryWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            category = s.toString();
        }
    };

    public void search() {
        Bundle b = new Bundle();
        b.putString("category", category);
        b.putString("q", state + " " + city);
        Intent i = new Intent(context, RestaurantResultsActivity.class);
        i.putExtras(b);
        context.startActivity(i);
    }
}