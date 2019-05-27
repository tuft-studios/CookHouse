package com.atta.cookhouse.orderdetails;

import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.Order;

import java.util.ArrayList;

public interface OrderDetailsContract {

    interface View{

        void showMessage(String error);

        void showRecyclerView(ArrayList<Order> orders);

        void showOrderDialog(Dish dish);

        void updateText();
    }

    interface Presenter{

        void getOrderDishes(int orderId) ;

    }
}
