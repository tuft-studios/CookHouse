package com.atta.cookhouse.myorders;

import android.content.Context;

import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.MyOrdersResult;
import com.atta.cookhouse.model.Order;
import com.atta.cookhouse.model.Result;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersPresenter implements MyOrdersContract.Presenter {

    private MyOrdersContract.View mView;

    private Context mContext;

    public MyOrdersPresenter(MyOrdersContract.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    @Override
    public void getMyOrders(int userId) {

        //defining the call
        Call<MyOrdersResult> call = APIClient.getInstance().getApi().getMyOrders(userId);

        //calling the api
        call.enqueue(new Callback<MyOrdersResult>() {
            @Override
            public void onResponse(Call<MyOrdersResult> call, Response<MyOrdersResult> response) {

                if (response.body() != null){
                    if (!response.body().getError()){

                        if (response.body().getOrders() != null){


                            ArrayList<Order> orders = response.body().getOrders();

                            if (orders.size() > 0){

                                mView.showRecyclerView(orders);
                            }else {

                                mView.updateText();
                            }
                        }else {

                            mView.updateText();
                        }

                    }
                }else {
                    mView.showMessage("An error");
                }

            }

            @Override
            public void onFailure(Call<MyOrdersResult> call, Throwable t) {

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
            public void onResponse(Call<Result> call, Response<Result> response) {
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

}
