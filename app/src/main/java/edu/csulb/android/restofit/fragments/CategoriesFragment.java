package edu.csulb.android.restofit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.adapters.CategoriesAdapter;
import edu.csulb.android.restofit.api.APIClient;
import edu.csulb.android.restofit.api.ZomatoAPI;
import edu.csulb.android.restofit.obseravables.FilterManager;
import edu.csulb.android.restofit.pojos.Category;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriesFragment extends SuperFragment implements Observer {

    private RecyclerView recyclerViewCategories;
    private List<Category> categoryList;
    private CategoriesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        recyclerViewCategories = (RecyclerView) view.findViewById(R.id.recycler_view_categories);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryList = new ArrayList<>();

        APIClient.getClient(ZomatoAPI.URL, true).create(ZomatoAPI.class).getCategories().enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        JSONArray json = new JSONObject(response.body().string()).getJSONArray("categories");
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject singleCategory = json.getJSONObject(i).getJSONObject("categories");
                            int id = singleCategory.getInt("id");
                            String name = singleCategory.getString("name");
                            categoryList.add(new Category(id, name));
                        }

                        adapter = new CategoriesAdapter(categoryList);
                        recyclerViewCategories.setAdapter(adapter);
                    } else {
                        System.out.println(response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return view;
    }

    @Override
    public void update(Observable observable, Object o) {
        String query = ((FilterManager) observable).getQuery();
        if (adapter != null)
            adapter.filter(query);
    }
}