package edu.csulb.android.restofit.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import edu.csulb.android.restofit.adapters.CategoryAdapter;
import edu.csulb.android.restofit.api.APIClient;
import edu.csulb.android.restofit.databinding.FragmentCategoriesBinding;
import edu.csulb.android.restofit.obseravables.FilterManager;
import edu.csulb.android.restofit.pojos.Category;
import edu.csulb.android.restofit.viewmodels.CategoryViewModel;

public class CategoriesFragment extends SuperFragment implements Observer {

    private CategoryViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final FragmentCategoriesBinding binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        binding.categories.setLayoutManager(new LinearLayoutManager(getContext()));
        final ArrayList<Category> categoryList = new ArrayList<>();
        CategoryAdapter adapter = new CategoryAdapter(categoryList, getContext());
        binding.categories.setAdapter(adapter);

        mViewModel = new CategoryViewModel(adapter);
        mViewModel.fetchCategories();

        return binding.getRoot();
    }

    @Override
    public void update(Observable observable, Object o) {
        String query = ((FilterManager) observable).getQuery();
        mViewModel.filter(query);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        APIClient.cancelAll();
    }
}