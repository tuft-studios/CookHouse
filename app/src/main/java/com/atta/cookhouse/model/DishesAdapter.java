package com.atta.cookhouse.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.atta.cookhouse.R;
import com.atta.cookhouse.fragments.FragmentsContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DishesAdapter extends RecyclerView.Adapter<DishesAdapter.MyViewHolder> {

    private List<Dish> dishes;

    FragmentsContract.View view ;

    Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, price, likes;
        public ImageView dishImage, add, likeImage;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.dish_name);
            dishImage = view.findViewById(R.id.dish_image);
            price = view.findViewById(R.id.price);
            add = view.findViewById(R.id.add);
            likes = view.findViewById(R.id.like_count);
            likeImage = view.findViewById(R.id.like);
        }
    }


    public DishesAdapter(FragmentsContract.View view, ArrayList<Dish> data, Context context) {

        this.dishes = data;
        this.view = view;
        this.context = context;
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

        final int id = dish.getDishId();
        final String name = dish.getDishName();
        final int price = dish.getPrice();
        final int likes = dish.getLikes();
        if (dish.getImageUrl() != null){

            final String imageURL = APIUrl.Images_BASE_URL + dish.getImageUrl();
            Picasso.get()
                    .load(imageURL)
                    .resize(180, 120)
                    .centerCrop()
                    .into(holder.dishImage);
        }
        holder.title.setText(name);
        String priceText = String.valueOf(price) + " " + context.getString(R.string.curruncy);
        holder.price.setText(priceText);
        holder.likes.setText(String.valueOf(likes));
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                view.showOrderDialog(dish);


            }
        });


        /*

        holder.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QueryUtils.addToFav(id);
            }
        });

        */

    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }
}
