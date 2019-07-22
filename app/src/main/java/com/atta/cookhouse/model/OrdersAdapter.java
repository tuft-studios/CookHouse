package com.atta.cookhouse.model;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.atta.cookhouse.R;
import com.atta.cookhouse.myorders.MyOrdersContract;

import java.util.ArrayList;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    private List<Order> orders;

    MyOrdersContract.View view ;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewOrderId;
        public TextView textViewOrderTime;
        public TextView textViewStatus;

        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);

            textViewOrderId = itemView.findViewById(R.id.textViewOrderId);
            textViewOrderTime = itemView.findViewById(R.id.textViewOrderTime);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            cardView = itemView.findViewById(R.id.cardViewId);
        }
    }

    public List<Order> getList(){
        return orders;
    }


    public OrdersAdapter(MyOrdersContract.View view, ArrayList<Order> orders) {

        this.orders = orders;
        this.view = view;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orders_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        final Order order = orders.get(position) ;

        String orderNumber = "Order Number - " + order.getId();

        final String orderDate = order.getCreationTime();
        final int statusValue = order.getStatus();


        switch (statusValue){
            case 0:
                holder.textViewStatus.setText("Waiting");
                holder.textViewStatus.setBackgroundResource(R.color.colorWaiting);
                break;
            case 1:
                holder.textViewStatus.setText("Cooking");
                holder.textViewStatus.setBackgroundResource(R.color.colorCooking);
                break;
            case 2:
                holder.textViewStatus.setText("Ready");
                holder.textViewStatus.setBackgroundResource(R.color.colorReady);
                break;
            case 3:
                holder.textViewStatus.setText("Delivered");
                holder.textViewStatus.setBackgroundResource(R.color.colorDelivered);
                break;
            case 4:
                holder.textViewStatus.setText("Canceled");
                holder.textViewStatus.setBackgroundResource(R.color.colorCanceled);
                break;
            default:
                holder.textViewStatus.setText("Waiting");
                holder.textViewStatus.setBackgroundResource(R.color.colorWaiting);
                break;
        }

        holder.textViewOrderId.setText(String.valueOf(order.getId()));
        holder.textViewOrderTime.setText(order.getOrderTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                view.showOrderDetails(order);


            }
        });


    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void removeItem(int position) {
        orders.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Order item, int position) {
        orders.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}
