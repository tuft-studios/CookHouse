package com.atta.cookhouse.orderdetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.cookhouse.R;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.DishesLinearAdapter;
import com.atta.cookhouse.model.Order;
import com.atta.cookhouse.myorders.MyOrdersActivity;

import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity implements OrderDetailsContract.View, View.OnClickListener {

    Order order;

    ArrayList<Dish> dishes;

    OrderDetailsPresenter orderDetailsPresenter;

    TextView timeTv, addressTv, mobileTv, subTotalTv, deliveryFeesTv, discountTv, totalTx;

    RecyclerView recyclerView;

    ImageView submittedImage, preparedImage, deliveredImage, backtoMain;

    DishesLinearAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);


        initiateViews();

        orderDetailsPresenter = new OrderDetailsPresenter(this, this);

        if (getIntent().getSerializableExtra("order") != null){
            order = (Order) getIntent().getSerializableExtra("order");

            orderDetailsPresenter.getOrderDishes(order.getId());
            setOrderData();



        }


    }

    @Override
    public void showMessage(String error) {

        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showRecyclerView(ArrayList<Dish> dishes) {
        this.dishes = dishes;

        myAdapter = new DishesLinearAdapter(this, this.dishes, order);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void showOrderDialog(Dish dish) {

    }

    @Override
    public void updateText() {

    }


    private void initiateViews() {
        // National ID, Password input text
        timeTv = findViewById(R.id.tv_delivery_time_sum);
        addressTv = findViewById(R.id.tv_address_sum);
        addressTv.setOnClickListener(this);
        mobileTv = findViewById(R.id.tv_phone_sum);
        mobileTv.setOnClickListener(this);
        subTotalTv = findViewById(R.id.tv_subtotal_sum);
        deliveryFeesTv = findViewById(R.id.tv_delivery_sum);
        discountTv = findViewById(R.id.tv_discount_sum);
        totalTx = findViewById(R.id.tv_total_sum);

        recyclerView = findViewById(R.id.recycler_cart2);


        submittedImage = findViewById(R.id.imageView);
        preparedImage = findViewById(R.id.imageView2);
        deliveredImage = findViewById(R.id.imageView3);

        backtoMain  = findViewById(R.id.btn_back_to_main);
        backtoMain.setOnClickListener(this);

    }

    private void setOrderData() {
        timeTv.setText(order.getOrderTime());
        addressTv.setText(order.getFullAddress().getFullAddress());
        mobileTv.setText(order.getMobile());
        subTotalTv.setText(order.getSubtotalPrice() + " EGP");
        deliveryFeesTv.setText(order.getDelivery() + " EGP");
        discountTv.setText(order.getDiscount() + " EGP");
        totalTx.setText(order.getTotalPrice() + " EGP");

        switch (order.getStatus()){
            case 0:
                submittedImage.setImageResource(R.drawable.dot_and_circle);
                preparedImage.setImageResource(R.drawable.circle);
                deliveredImage.setImageResource(R.drawable.circle);
                preparedImage.setOnClickListener(this);
                break;

            case 1:
                submittedImage.setImageResource(R.drawable.circle);
                preparedImage.setImageResource(R.drawable.dot_and_circle);
                deliveredImage.setImageResource(R.drawable.circle);
                preparedImage.setOnClickListener(null);
                deliveredImage.setOnClickListener(this);
                break;

            case 2:
                submittedImage.setImageResource(R.drawable.circle);
                preparedImage.setImageResource(R.drawable.circle);
                deliveredImage.setImageResource(R.drawable.dot_and_circle);
                preparedImage.setOnClickListener(null);
                deliveredImage.setOnClickListener(null);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == backtoMain){

            Intent intent = new Intent(this, MyOrdersActivity.class);
            startActivity(intent);
        }
    }
}
