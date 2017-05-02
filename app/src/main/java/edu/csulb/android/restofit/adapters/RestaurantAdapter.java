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
import edu.csulb.android.restofit.activities.RestaurantActivity;
import edu.csulb.android.restofit.helpers.StaticMembers;
import edu.csulb.android.restofit.pojos.Restaurant;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> restaurants, filteredRestaurants = new ArrayList<>();

    public RestaurantAdapter(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        filteredRestaurants.addAll(this.restaurants);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Restaurant restaurant = filteredRestaurants.get(position);
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
        return filteredRestaurants.size();
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