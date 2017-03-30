package edu.csulb.android.restofit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.adapters.CategoriesAdapter;
import edu.csulb.android.restofit.obseravables.FilterManager;
import edu.csulb.android.restofit.pojos.Category;

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
        categoryList.add(new Category(1, "Delivery"));
        categoryList.add(new Category(2, "Dine-out"));
        categoryList.add(new Category(3, "Nightlife"));
        categoryList.add(new Category(4, "Catching-up"));
        categoryList.add(new Category(5, "Takeaway"));
        categoryList.add(new Category(6, "Cafes"));
        categoryList.add(new Category(7, "Daily Menus"));
        categoryList.add(new Category(8, "Breakfast"));
        categoryList.add(new Category(9, "Lunch"));
        categoryList.add(new Category(10, "Dinner"));
        categoryList.add(new Category(11, "Pubs & Bars"));
        categoryList.add(new Category(13, "Pocket Friendly Delivery"));
        categoryList.add(new Category(14, "Clubs & Lounges"));

        adapter = new CategoriesAdapter(categoryList);
        recyclerViewCategories.setAdapter(adapter);

        return view;
    }

    @Override
    public void update(Observable observable, Object o) {
        String query = ((FilterManager) observable).getQuery();
        adapter.filter(query);
    }
}