package com.atta.cookhouse.model;

import android.support.annotation.NonNull;
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
        public TextView id, date, status, orderInfo;

        public MyViewHolder(View view) {
            super(view);
            id = view.findViewById(R.id.order_num);
            date = view.findViewById(R.id.order_date);
            status = view.findViewById(R.id.status_tv);
            orderInfo = view.findViewById(R.id.details_btn);
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
                holder.status.setText("Waiting");

                holder.status.setBackgroundResource(R.drawable.waiting_status_shape);
                break;
            case 1:
                holder.status.setText("Received");

                holder.status.setBackgroundResource(R.drawable.received_status_shape);
                break;
            case 2:
                holder.status.setText("Ready");

                holder.status.setBackgroundResource(R.drawable.ready_status_shape);
                break;
            case 3:
                holder.status.setText("Delivered");

                holder.status.setBackgroundResource(R.drawable.delivered_status_shape);
                break;
            case 4:
                holder.status.setText("Cancelled");

                holder.status.setBackgroundResource(R.drawable.cancelled_status_shape);
                break;
            default:
                holder.status.setText("Waiting");

                holder.status.setBackgroundResource(R.drawable.waiting_status_shape);
                break;
        }

        holder.id.setText(orderNumber);
        holder.date.setText(orderDate);
        holder.orderInfo.setOnClickListener(new View.OnClickListener() {
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
