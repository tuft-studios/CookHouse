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
import com.atta.cookhouse.orderdetails.OrderDetailsContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DishesLinearAdapter extends RecyclerView.Adapter<DishesLinearAdapter.MyViewHolder> {

    private List<Dish> dishes;

    Order order;

    FavoritesContract.View favView ;

    OrderDetailsContract.View orderView;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, price, count;
        public ImageView dishImage;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.name_txt);
            if (favView != null){
                dishImage = view.findViewById(R.id.image);
                price = view.findViewById(R.id.price_txt);
                viewBackground = view.findViewById(R.id.view_background);
                viewForeground = view.findViewById(R.id.view_foreground);
            }else count = view.findViewById(R.id.count_sam);

        }
    }

    public List<Dish> getList(){
        return dishes;
    }


    public DishesLinearAdapter(FavoritesContract.View view, ArrayList<Dish> data) {

        this.dishes = data;
        this.favView = view;
    }

    public DishesLinearAdapter(OrderDetailsContract.View view, ArrayList<Dish> data, Order order) {

        this.dishes = data;
        this.orderView = view;
        this.order = order;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (favView != null) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fav_list_item, parent, false);
        }else {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sam_list_item, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        final Dish dish = dishes.get(position) ;

        final int id = dish.getDishId();
        final String name = dish.getDishName();
        final int price = dish.getPrice();

        holder.title.setText(name);

        if (favView != null) {

            if (dish.getImageUrl() != null) {

                final String imageURL = APIUrl.Images_BASE_URL + dish.getImageUrl();
                Picasso.get()
                        .load(imageURL)
                        .resize(50, 50)
                        .centerCrop()
                        .into(holder.dishImage);
            }

            holder.price.setText(String.valueOf(price) + " EGP");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    favView.showOrderDialog(dish);


                }
            });

        }else {
            int count = order.getDishesList().get(position).getQuantity();

            holder.count.setText("X" + count);
        }



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
