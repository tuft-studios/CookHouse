package com.atta.cookhouse.main;

import android.support.v7.widget.RecyclerView;

import com.atta.cookhouse.fragments.FragmentsPresenter;
import com.atta.cookhouse.model.Dish;

public interface MainContract {



    interface View{

        void showMessage(String error);

        void setFlipperView();

        void renameRegisterItem();

        void renameLoginItem();

        void checkIfSkipped();

        void setName();

        void showOrderDialog(Dish dish, FragmentsPresenter fragmentsPresenter, RecyclerView recyclerView, String type, String location);

        void showPasswordDialog();

        boolean validate(String currentPassword, String newPassword, String newPasswordConfirm);

        void changeFavIcon(boolean isFav);

        void setFavId(int id);

        void setPoints(int points);

        void setDialog();
    }

    interface Presenter{

        void getCartItemsNum() ;

        void addToFav(int dishId, int userId);

        void removeFromFav(int fId);

        void checkIfFav(int dishId, int userId);

        void saveToken(String token, int userId);

        void getPoints(int userId);

    }
}
