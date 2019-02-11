package com.atta.cookhouse.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.atta.cookhouse.R;
import com.atta.cookhouse.cart.CartContract;
import com.atta.cookhouse.cart.CartPresenter;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.TasksViewHolder>  {


    private Context mCtx;
    private List<CartItem> cartItems;
    private CartPresenter cartPresenter;

    CartContract.View mView;

    public CartAdapter(Context mCtx, List<CartItem> cartItems, CartPresenter cartPresenter, CartContract.View mView) {
        this.mCtx = mCtx;
        this.cartItems = cartItems;
        this.cartPresenter = cartPresenter;
        this.mView = mView;
    }

    @Override
    public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.cart_item, parent, false);
        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TasksViewHolder holder, final int position) {
        final CartItem cartItem = cartItems.get(position);

        int price = Integer.valueOf(cartItem.getDishPrice()) * cartItem.getCount();

        holder.textViewDishName.setText(cartItem.getDishName());
        holder.textViewDishPrice.setText(String.valueOf(price) + " EGP");
        holder.textViewDishCount.setText(String.valueOf(cartItem.getCount()));


        holder.addOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cartPresenter.updateCount(cartItem, true);

                holder.textViewDishCount.setText(String.valueOf(cartItem.getCount()));

                int price = Integer.valueOf(cartItem.getDishPrice())* cartItem.getCount();

                holder.textViewDishPrice.setText(String.valueOf(price) + " EGP");


                mView.setTotalPrice(cartPresenter.totalPriceCalculation(cartItems), 5);


            }
        });
        holder.removeOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cartItem.getCount() != 1){
                    int count = cartItem.getCount();

                    cartItem.setCount(count - 1);

                    cartPresenter.updateCount(cartItem, false);

                    holder.textViewDishCount.setText(String.valueOf(cartItem.getCount()));

                    int price = Integer.valueOf(cartItem.getDishPrice())* cartItem.getCount();

                    holder.textViewDishPrice.setText(String.valueOf(price) + " EGP");

                    mView.setTotalPrice(cartPresenter.totalPriceCalculation(cartItems), 5);


                }else {

                    cartPresenter.deleteCartItem(cartItem);
                    cartItems.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, cartItems.size());


                    if (cartItems.size() == 0){
                        mView.backToMain();
                    }else {

                        mView.setTotalPrice(cartPresenter.totalPriceCalculation(cartItems), 5);
                    }
                }
            }
        });
        holder.removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cartPresenter.deleteCartItem(cartItem);
                cartItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());

                if (cartItems.size() == 0){
                    mView.backToMain();
                }else {

                    mView.setTotalPrice(cartPresenter.totalPriceCalculation(cartItems), 5);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class TasksViewHolder  extends RecyclerView.ViewHolder {


        TextView textViewDishName, textViewDishPrice, textViewDishCount;

        ImageView addOne, removeOne, removeItem;

        public TasksViewHolder(View itemView) {
            super(itemView);

            textViewDishName = itemView.findViewById(R.id.item_name);
            textViewDishPrice = itemView.findViewById(R.id.item_price);
            textViewDishCount = itemView.findViewById(R.id.count);

            addOne = itemView.findViewById(R.id.add_one);
            removeOne = itemView.findViewById(R.id.remove_one);
            removeItem = itemView.findViewById(R.id.remove_item);


        }

    }
}
