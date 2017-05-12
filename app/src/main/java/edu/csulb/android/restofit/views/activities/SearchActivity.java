package edu.csulb.android.restofit.views.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.ilhasoft.support.validation.Validator;
import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.databinding.ActivitySearchBinding;
import edu.csulb.android.restofit.helpers.PreferenceHelper;
import edu.csulb.android.restofit.helpers.PreferenceKeys;
import edu.csulb.android.restofit.viewmodels.SearchViewModel;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private SearchViewModel model;
    private Validator validator;
    private List<String> CATEGORIES_LIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySearchBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        model = new SearchViewModel(getApplicationContext());
        binding.setModel(model);

        validator = new Validator(binding);
        binding.buttonSearch.setOnClickListener(this);

        CATEGORIES_LIST = new ArrayList<>();
        try {
            JSONArray json = new JSONArray(PreferenceHelper.getString(PreferenceKeys.CATEGORIES));
            for (int i = 0; i < json.length(); i++) {
                JSONObject singleCategory = json.getJSONObject(i).getJSONObject("categories");
                String name = singleCategory.getString("name");
                System.out.println("name: " + name);
                CATEGORIES_LIST.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, CATEGORIES_LIST);
        binding.autoCategory.setAdapter(adapter);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (validator.validate()) {
            model.search();
        }
    }
}