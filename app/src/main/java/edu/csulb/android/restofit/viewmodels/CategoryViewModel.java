package edu.csulb.android.restofit.viewmodels;

import android.databinding.BaseObservable;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.csulb.android.restofit.adapters.CategoryAdapter;
import edu.csulb.android.restofit.api.APIClient;
import edu.csulb.android.restofit.api.ZomatoAPI;
import edu.csulb.android.restofit.pojos.Category;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewModel extends BaseObservable {

    private CategoryAdapter mAdapter;

    public CategoryViewModel(CategoryAdapter adapter) {
        this.mAdapter = adapter;
    }

    public void fetchCategories() {
        APIClient.getClient(ZomatoAPI.URL, true).create(ZomatoAPI.class).getCategories().enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        JSONArray json = new JSONObject(response.body().string()).getJSONArray("categories");
                        mAdapter.clear();
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject singleCategory = json.getJSONObject(i).getJSONObject("categories");
                            int id = singleCategory.getInt("id");
                            String name = singleCategory.getString("name");
                            mAdapter.add(new Category(id, name));
                        }
                        mAdapter.notifyDataSetChanged();
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
    }

    public void filter(String query) {
        mAdapter.filter(query);
    }
}