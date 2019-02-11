package com.atta.cookhouse.cart;

import android.widget.EditText;

import com.atta.cookhouse.model.CartItem;
import com.atta.cookhouse.model.Order;

import java.util.List;

public interface CartContract {

    interface View{

        void backToMain();

        void enableOrderBtn();

        void disableOrderBtn();

        void setTotalPrice(int subtotal, int delivery);

        void showMessage(String message);

        void setProgressDialog();

        void navigateToMain();

        void createOrder(String deliveryAdd);

        void showDialog();

        boolean validateOrder(String deliveryAdd, EditText deliveryAddress, EditText timeText, EditText dateText);


        void dismissProgressDialog();

        void navigateToRegister();

        void showViewError(String view, String error);

    }

    interface Presenter{

        void getCartItems(boolean view, int userId, String location, String deliveryAdd, String schedule, String orderTime);

        int totalPriceCalculation(List<CartItem> cartItems);

        void removeCartItems();

        void deleteCartItem(CartItem task);

        void updateCount(CartItem cartItem, boolean add);

        void addOrder(Order order);

        void login(String email, String password);

        boolean validate(String email, String password);
    }
}
