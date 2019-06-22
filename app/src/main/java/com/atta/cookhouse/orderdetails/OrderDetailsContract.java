package com.atta.cookhouse.orderdetails;

import com.atta.cookhouse.model.Dish;

import java.util.ArrayList;

public interface OrderDetailsContract {

    interface View{

        void showMessage(String error);

        void showRecyclerView(ArrayList<Dish> dishes);

        void showOrderDialog(Dish dish);

        void updateText();
    }

    interface Presenter{

        void getOrderDishes(int orderId) ;

    }
}
