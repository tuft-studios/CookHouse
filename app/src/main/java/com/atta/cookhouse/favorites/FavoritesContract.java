package com.atta.cookhouse.favorites;

import com.atta.cookhouse.model.Dish;

import java.util.ArrayList;

public interface FavoritesContract {

    interface View{

        void showMessage(String error);

        void showRecyclerView(ArrayList<Dish> dishes );

        void showOrderDialog(Dish dish);

        void updateText();
    }

    interface Presenter{
        void getFavDishes(int userId) ;

        void removeFromFav(int userId, int dishId);

    }
}
