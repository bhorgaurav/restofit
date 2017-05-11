package edu.csulb.android.restofit.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;

import java.util.List;

import edu.csulb.android.restofit.databinding.ItemReviewBinding;
import edu.csulb.android.restofit.pojos.Review;

public class ReviewAdapter extends ArrayAdapter<Review> {

    private Context context;
    private List<Review> objects;

    public ReviewAdapter(@NonNull Context context, @NonNull List<Review> objects) {
        super(context, 0, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Nullable
    @Override
    public Review getItem(int position) {
        return objects.get(position);
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        Review review = getItem(position);
        ItemReviewBinding binding = ItemReviewBinding.inflate(inflater, parent, false);
        binding.setReview(review);
        System.out.println("review.getPhotoUrl() : " + review.getPhotoUrl());
        if (review.getPhotoUrl() != null) {
            Picasso.with(context).load(review.getPhotoUrl()).into(binding.imageViewReviewPhoto);
        } else {
            binding.imageViewReviewPhoto.setVisibility(View.GONE);
        }
        return binding.getRoot();
    }
}