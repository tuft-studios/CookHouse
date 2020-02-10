package com.atta.cookhouse.favorites;

import android.content.Context;
import android.os.AsyncTask;

import com.atta.cookhouse.Local.DatabaseClient;
import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.CartItem;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.Dishes;
import com.atta.cookhouse.model.Result;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesPresenter implements FavoritesContract.Presenter {

    private FavoritesContract.View mView;

    private Context mContext;

    public FavoritesPresenter(FavoritesContract.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    @Override
    public void getFavDishes(int userId) {

        //defining the call
        Call<Dishes> call = APIClient.getInstance().getApi().getFavorite(userId);

        //calling the api
        call.enqueue(new Callback<Dishes>() {
            @Override
            public void onResponse(Call<Dishes> call, Response<Dishes> response) {

                if (response.body() != null){
                    if (response.body().getDishes() != null){

                        ArrayList<Dish> dishes = response.body().getDishes();

                        if (dishes.size() > 0){

                            mView.showRecyclerView(dishes);
                        }else {

                            mView.updateText();
                        }

                    }
                }else {
                    mView.showMessage("An error");
                }

            }

            @Override
            public void onFailure(Call<Dishes> call, Throwable t) {

                mView.showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void removeFromFav(int userId, int dishId) {

        //defining the call
        Call<Result> call = APIClient.getInstance().getApi().removeFromFavorite(userId, dishId);

        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                //hiding progress dialog


                //displaying the message from the response as toast
                if (response.body() != null) {

                    mView.showMessage(response.body().getMessage());

                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

                    mView.showMessage(t.getMessage());

            }
        });
    }


    @Override
    public void checkCartItem(final Dish dish) {


        class GetTasks extends AsyncTask<Void, Void, CartItem> {

            @Override
            protected CartItem doInBackground(Void... voids) {
                CartItem cartItem = DatabaseClient
                        .getInstance(mContext)
                        .getAppDatabase()
                        .itemDao()
                        .checkItem(dish.getDishId(), dish.getSelectedOption(), dish.getSelectedSide1() ,
                                dish.getSelectedSide2(), dish.getSelectedSize());
                return cartItem;
            }

            @Override
            protected void onPostExecute(CartItem cartItem) {
                super.onPostExecute(cartItem);

                if (cartItem != null) {
                    mView.setCount(cartItem.getCount(), dish);
                }else mView.setCount(0, dish);


            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();



    }


}
