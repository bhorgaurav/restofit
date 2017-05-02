package edu.csulb.android.restofit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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

    @BindView(R.id.recycler_view_categories)
    RecyclerView recyclerViewCategories;

    private List<Category> categoryList;
    private CategoriesAdapter adapter;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        unbinder = ButterKnife.bind(this, view);

        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryList = new ArrayList<>();

        APIClient.getClient(ZomatoAPI.URL, true).create(ZomatoAPI.class).getCategories().enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (isVisible())
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
                            Toast.makeText(getContext(), "Error loading categories. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error loading categories. Please try again.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        APIClient.cancelAll();
        unbinder.unbind();
    }
}