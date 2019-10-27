package com.atta.cookhouse.orderdetails;

import android.content.Context;

import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.Dishes;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsPresenter implements OrderDetailsContract.Presenter {

    private OrderDetailsContract.View mView;

    private Context mContext;

    public OrderDetailsPresenter(OrderDetailsContract.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    @Override
    public void getOrderDishes(int orderId) {

        //defining the call
        Call<Dishes> call = APIClient.getInstance().getApi().getOrderDishes(orderId);

        //calling the api
        call.enqueue(new Callback<Dishes>() {
            @Override
            public void onResponse(Call<Dishes> call, Response<Dishes> response) {

                if (response.body() != null){

                        if (response.body().getDishes() != null){


                            ArrayList<Dish> dishes = response.body().getDishes();

                            if (dishes.size() > 0){

                                mView.showRecyclerView(dishes);
                            }
                        }else {
                            mView.showMessage("An error");
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


}
