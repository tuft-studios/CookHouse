package com.atta.cookhouse.model;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atta.cookhouse.R;
import com.atta.cookhouse.favorites.FavoritesContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DishesLinearAdapter extends RecyclerView.Adapter<DishesLinearAdapter.MyViewHolder> {

    private List<Dish> dishes;

    FavoritesContract.View view ;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, price;
        public ImageView dishImage;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.name_txt);
            dishImage = view.findViewById(R.id.image);
            price = view.findViewById(R.id.price_txt);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }

    public List<Dish> getList(){
        return dishes;
    }


    public DishesLinearAdapter(FavoritesContract.View view, ArrayList<Dish> data) {

        this.dishes = data;
        this.view = view;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fav_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        final Dish dish = dishes.get(position) ;

        final int id = dish.getDishId();
        final String name = dish.getDishName();
        final int price = dish.getPrice();
        if (dish.getImageUrl() != null){

            final String imageURL = APIUrl.Images_BASE_URL + dish.getImageUrl();
            Picasso.get()
                    .load(imageURL)
                    .resize(50, 50)
                    .centerCrop()
                    .into(holder.dishImage);
        }
        holder.title.setText(name);
        holder.price.setText(String.valueOf(price) + " EGP");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                view.showOrderDialog(dish);


            }
        });


    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    public void removeItem(int position) {
        dishes.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Dish item, int position) {
        dishes.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}
