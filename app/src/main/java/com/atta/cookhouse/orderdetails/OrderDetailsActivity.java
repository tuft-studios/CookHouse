package com.atta.cookhouse.orderdetails;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.atta.cookhouse.R;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.Order;

import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity implements OrderDetailsContract.View{

    Order order;

    ArrayList<Dish> dishes;

    OrderDetailsPresenter orderDetailsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        orderDetailsPresenter = new OrderDetailsPresenter(this, this);

        if (getIntent().getSerializableExtra("order") != null){
            order = (Order) getIntent().getSerializableExtra("order");

            orderDetailsPresenter.getOrderDishes(order.getId());

        }
    }

    @Override
    public void showMessage(String error) {

    }

    @Override
    public void showRecyclerView(ArrayList<Order> orders) {

    }

    @Override
    public void showOrderDialog(Dish dish) {

    }

    @Override
    public void updateText() {

    }
}
