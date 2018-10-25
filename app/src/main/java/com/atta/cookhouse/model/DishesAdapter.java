package com.atta.cookhouse.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.cookhouse.R;
import com.atta.cookhouse.main.MainPresenter;

import java.util.ArrayList;
import java.util.List;

public class DishesAdapter extends RecyclerView.Adapter<DishesAdapter.MyViewHolder> {

    private Context mContext;

    private List<Dish> dishes;

    MainPresenter mainPresenter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, medPrice, largePrice;
        public ImageView dishImage,addMed, addLarge;
        RatingBar ratingBar;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.dish_name);
            ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);
            dishImage = (ImageView) view.findViewById(R.id.dish_image);
            medPrice = (TextView) view.findViewById(R.id.medium_price);
            largePrice = (TextView) view.findViewById(R.id.large_price);
            addMed = (ImageView) view.findViewById(R.id.add_med);
            addLarge = (ImageView) view.findViewById(R.id.add_large);
        }
    }


    public DishesAdapter(Context context, MainPresenter mainPresenter, ArrayList<Dish> data) {

        this.mContext = context;
        this.dishes = data;
        this.mainPresenter = mainPresenter;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        final Dish dish = dishes.get(position) ;

        final String name = dish.getDishName();
        final int mediumPrice = dish.getMediumPrice();
        final int largePrice = dish.getLargePrice();
        final String ImageURL = dish.getImageUrl();
        mainPresenter.getRetrofitImage(holder.dishImage, ImageURL);
        final int rating = dish.getRating();
        holder.title.setText(name);
        holder.medPrice.setText(String.valueOf(mediumPrice));
        holder.largePrice.setText(String.valueOf(largePrice));
        holder.ratingBar.setRating(rating);
        holder.addMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext,"medium add",Toast.LENGTH_LONG).show();
            }
        });


        holder.addLarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext,"large add",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }
}
