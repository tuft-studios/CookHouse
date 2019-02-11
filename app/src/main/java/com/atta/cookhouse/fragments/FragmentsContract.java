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
    }

    interface Presenter{


        void getMenu(final RecyclerView recyclerView, String type, String location);

        void getCartItem(int id, String dishName, String price, int count);

    }
}
