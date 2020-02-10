package com.atta.cookhouse.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.andremion.counterfab.CounterFab;
import com.atta.cookhouse.Local.DatabaseClient;
import com.atta.cookhouse.Local.QueryUtils;
import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.AddFavResult;
import com.atta.cookhouse.model.CartItem;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.Dishes;
import com.atta.cookhouse.model.FavResult;
import com.atta.cookhouse.model.Result;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class FragmentsPresenter implements FragmentsContract.Presenter {


    private FragmentsContract.View mView;

    private Context mContext;

    CounterFab mCounterFab;

    public FragmentsPresenter(FragmentsContract.View view, Context context, CounterFab counterFab) {

        mView = view;

        mContext = context;

        mCounterFab = counterFab;
    }


    @Override
    public void getMenu(final RecyclerView recyclerView, int type, String location) {

        ArrayList<Dish> dishes = null;

        //defining the call
        retrofit2.Call<Dishes> call = APIClient.getInstance().getApi().getMenu(type, location);

        //calling the api
        call.enqueue(new retrofit2.Callback<Dishes>() {
            @Override
            public void onResponse(retrofit2.Call<Dishes> call, retrofit2.Response<Dishes> response) {

                if (response.body() != null){
                    if (response.body().getDishes() != null){

                        ArrayList<Dish> dishes = response.body().getDishes();

                        if (dishes.size() > 0){

                            //checkCartItems(dishes);


                            mView.showRecyclerView(dishes);
                        }

                    }
                }else {
                    mView.showError("An error");
                }



            }

            @Override
            public void onFailure(retrofit2.Call<Dishes> call, Throwable t) {

                mView.showError(t.getMessage());
            }
        });

    }


    @Override
    public void checkCartItems(final ArrayList<Dish> dishes) {


        for (int i = 0; i < dishes.size(); ++ i) {


            final Dish dish = dishes.get(i);

            final int finalI = i;
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

                        dish.setCartCount(cartItem.getCount());
                    }


                    if (finalI == dishes.size()-1){


                        mView.showRecyclerView(dishes);
                    }

                }
            }

            GetTasks gt = new GetTasks();
            gt.execute();

        }

    }




    @Override
    public void checkCartItem(final Dish dish) {


        class GetTasks extends AsyncTask<Void, Void, CartItem> {

            @Override
            protected CartItem doInBackground(Void... voids) {
                return DatabaseClient
                        .getInstance(mContext)
                        .getAppDatabase()
                        .itemDao()
                        .checkItem(dish.getDishId(), dish.getSelectedOption(),
                                dish.getSelectedSide1() ,dish.getSelectedSide2(),
                                dish.getSelectedSize());
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

    @Override
    public void addToCard(final Dish dish, final String price, final int count) {

        QueryUtils.checkCartItem(dish, price, count, mContext, mCounterFab);


    }


    @Override
    public void checkIfFav(int propertyId, int userId) {

        //defining the call
        Call<FavResult> call = APIClient.getInstance().getApi().checkIfFavorite(propertyId, userId);

        //calling the api
        call.enqueue(new Callback<FavResult>() {
            @Override
            public void onResponse(Call<FavResult> call, retrofit2.Response<FavResult> response) {


                //displaying the message from the response as toast
                if (response.body() != null){

                    mView.changeFavIcon(response.body().getFavorite());

                    if (response.body().getFavorite()){
                        mView.setFavId(response.body().getId());
                    }

                }

            }

            @Override
            public void onFailure(Call<FavResult> call, Throwable t) {

                mView.showMessage(t.getMessage());
            }
        });
    }


    @Override
    public void addToFav(int propertyId, int userId) {

        //defining the call
        Call<AddFavResult> call = APIClient.getInstance().getApi().addToFavorite(propertyId, userId);

        //calling the api
        call.enqueue(new Callback<AddFavResult>() {
            @Override
            public void onResponse(Call<AddFavResult> call, retrofit2.Response<AddFavResult> response) {
                //hiding progress dialog


                //displaying the message from the response as toast
                if (response.body() != null) {
                    mView.showMessage(response.body().getMessage());
                    //if there is no error
                    if (!response.body().getError()) {
                        //starting Main activity
                        mView.changeFavIcon(true);
                        mView.setFavId(response.body().getId());
                    }else {
                        mView.showMessage(response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AddFavResult> call, Throwable t) {

                mView.showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void removeFromFav(int dishId, int userId) {


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
                    //if there is no error
                    if (!response.body().getError()) {
                        //starting Main activity
                        mView.changeFavIcon(false);
                        //mView.setFavId(response.body().getId());
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

                mView.showMessage(t.getMessage());
            }
        });
    }




}
