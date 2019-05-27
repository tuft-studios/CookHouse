package com.atta.cookhouse.myorders;

import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.Order;

import java.util.ArrayList;

public interface MyOrdersContract {

    interface View{

        void showMessage(String error);

        void showRecyclerView(ArrayList<Order> orders);

        void showOrderDialog(Dish dish);

        void updateText();

        void showOrderDetails(Order order);
    }

    interface Presenter{

        void getMyOrders(int userId) ;

        void removeFromFav(int userId, int dishId);

    }
}
