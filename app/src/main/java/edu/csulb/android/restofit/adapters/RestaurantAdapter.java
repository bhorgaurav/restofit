package edu.csulb.android.restofit.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.csulb.android.restofit.R;
import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.pojos.Restaurant;
import edu.csulb.android.restofit.views.activities.RestaurantActivity;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> mRestaurants, mFilteredRestaurants = new ArrayList<>();

    public RestaurantAdapter(List<Restaurant> mRestaurants) {
        this.mRestaurants = mRestaurants;
        mFilteredRestaurants.addAll(this.mRestaurants);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Restaurant restaurant = mFilteredRestaurants.get(position);
        holder.name.setText(restaurant.name);
        holder.cuisines.setText("Cuisine: " + restaurant.cuisines);
        holder.aggregate_rating.setText(restaurant.aggregate_rating);
        holder.aggregate_rating.setBackgroundColor(Color.parseColor("#" + restaurant.rating_color));
        holder.address.setText(restaurant.address);
        holder.average_cost.setText("Cost for 2: " + restaurant.currency + restaurant.average_cost);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), RestaurantActivity.class);
                i.putExtra(StaticMembers.IntentFlags.RESTAURANT, restaurant);
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredRestaurants.size();
    }

    public void add(Restaurant restaurants) {
        this.mRestaurants.add(restaurants);
        this.mFilteredRestaurants.add(restaurants);
        notifyDataSetChanged();
    }

    public void addAll(List<Restaurant> restaurants) {
        int size = this.mFilteredRestaurants.size();
        this.mRestaurants.addAll(restaurants);
        this.mFilteredRestaurants.addAll(restaurants);
        notifyItemMoved(size - 1, this.mFilteredRestaurants.size());
    }

    public void clear() {
        int size = this.mFilteredRestaurants.size();
        this.mFilteredRestaurants.clear();
        this.mRestaurants.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void filter(String text) {
        mFilteredRestaurants.clear();
        if (text.isEmpty()) {
            mFilteredRestaurants.addAll(mRestaurants);
        } else {
            text = text.toLowerCase();
            for (Restaurant item : mRestaurants) {
                if (item.name.toLowerCase().contains(text) || item.cuisines.toLowerCase().contains(text) || item.address.toLowerCase().contains(text)) {
                    mFilteredRestaurants.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_view_restaurant_name)
        TextView name;

        @BindView(R.id.text_view_average_cost)
        TextView average_cost;

        @BindView(R.id.text_view_address)
        TextView address;

        @BindView(R.id.text_view_restaurant_rating)
        TextView aggregate_rating;

        @BindView(R.id.text_view_cuisine)
        TextView cuisines;

        @BindView(R.id.card_view_restaurant)
        CardView cardView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}