package com.atta.cookhouse.fragments;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;

import com.atta.cookhouse.model.Dish;

import java.util.ArrayList;

public interface FragmentsContract {

    interface View{

        void showError(String error);

        void ViewImage(Bitmap bitmap);

        void openCart();

        void showOrderDialog(Dish dish);

        void showRecyclerView(ArrayList<Dish> dishes );

        void setFavId(int id);

        void changeFavIcon(boolean isFav);

        void showMessage(String error);

        void setDialog();

        void setCount(int count, Dish dish);
    }

    interface Presenter{


        void getMenu(final RecyclerView recyclerView, int type, String location);

        void getCartItem(int id, String dishName, String price, int count);

        void checkIfFav(int propertyId, int userId);

        void addToFav(int dishId, int userId);

        void removeFromFav(int dishId, int userId);

        void checkCartItems(final ArrayList<Dish> dishes);

        void checkCartItem(final Dish dish);

    }
}
