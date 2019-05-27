package com.atta.cookhouse.myorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.cookhouse.orderdetails.OrderDetailsActivity;
import com.atta.cookhouse.R;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.Order;
import com.atta.cookhouse.model.OrdersAdapter;
import com.atta.cookhouse.model.SessionManager;

import java.util.ArrayList;

public class MyOrdersActivity extends AppCompatActivity implements MyOrdersContract.View{

    MyOrdersPresenter myOrdersPresenter;

    RecyclerView recyclerView;

    ConstraintLayout noOrders;

    TextView infoTextView;

    OrdersAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        myOrdersPresenter = new MyOrdersPresenter(this, this);

        recyclerView = findViewById(R.id.orders_recycler);

        infoTextView = findViewById(R.id.orders_info_tv);

        noOrders = findViewById(R.id.orders_layout);

        if (SessionManager.getInstance(this).isLoggedIn()){

            myOrdersPresenter.getMyOrders(SessionManager.getInstance(this).getUserId());

            final SwipeRefreshLayout mySwipeRefreshLayout = findViewById(R.id.orders_refresh);

            mySwipeRefreshLayout.setOnRefreshListener(
                    () -> {
                        myOrdersPresenter.getMyOrders(SessionManager.getInstance(this).getUserId());
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
            );
        }else {
            infoTextView.setText("You need to login to see your orders");
        }

    }

    @Override
    public void showMessage(String error) {

        Toast.makeText(this,error,Toast.LENGTH_LONG).show();
    }

    @Override
    public void showRecyclerView(ArrayList<Order> orders) {

        recyclerView.setVisibility(View.VISIBLE);
        noOrders.setVisibility(View.GONE);
        myAdapter = new OrdersAdapter(this, orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myAdapter);

    }

    @Override
    public void showOrderDialog(Dish dish) {

    }

    @Override
    public void updateText() {

        recyclerView.setVisibility(View.GONE);
        noOrders.setVisibility(View.VISIBLE);
        infoTextView.setText("No orders added");
    }

    @Override
    public void showOrderDetails(Order order) {

        Intent intent = new Intent(MyOrdersActivity.this, OrderDetailsActivity.class);
        intent.putExtra("order", order);
        startActivity(intent);
    }
}
