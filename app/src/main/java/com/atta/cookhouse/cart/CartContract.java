package com.atta.cookhouse.cart;

import android.widget.EditText;

import com.atta.cookhouse.model.Address;
import com.atta.cookhouse.model.CartItem;
import com.atta.cookhouse.model.Order;

import java.util.List;

public interface CartContract {

    interface View{

        void backToMain();

        void enableOrderBtn();

        void disableOrderBtn();

        void setTotalPrice(double subtotal, double delivery);

        void showMessage(String message);

        void setProgressDialog();

        void navigateToMain();

        void createOrder(int deliveryAdd);

        void showDialog();

        boolean validateOrder(int deliveryAdd, EditText timeText, EditText dateText);

        void dismissProgressDialog();

        void navigateToRegister();

        void showViewError(String view, String error);

        void showAddresses(List<Address> mAddresses);

        void showAddressesMessage(String message);

        void setDiscount(int discount);

        void wrongPromo();
    }

    interface Presenter{

        void getCartItems(boolean view, int userId, String location, int deliveryAdd, String mobile,
                          String orderTime, double discountAmount, String promocode, int numOfPoints);

        double totalPriceCalculation(List<CartItem> cartItems);

        void removeCartItems();

        void deleteCartItem(CartItem task);

        void updateCount(CartItem cartItem, boolean add);

        void addOrder(Order order);

        void login(String email, String password);

        boolean validate(String email, String password);

        void getAddresses(int userId);

        void checkPromoCode(int userId, String promoCode);
    }
}
