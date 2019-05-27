package com.atta.cookhouse.myorders;

import android.content.Context;

import com.atta.cookhouse.model.APIService;
import com.atta.cookhouse.model.APIUrl;
import com.atta.cookhouse.model.MyOrdersResult;
import com.atta.cookhouse.model.Order;
import com.atta.cookhouse.model.Result;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyOrdersPresenter implements MyOrdersContract.Presenter {

    private MyOrdersContract.View mView;

    private Context mContext;

    public MyOrdersPresenter(MyOrdersContract.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    @Override
    public void getMyOrders(int userId) {

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);

        //Defining the user object as we need to pass it with the call
        //User user = new User(name, email, password, phone, birthdayString, locationSting);

        //defining the call
        Call<MyOrdersResult> call = service.getMyOrders(userId);

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

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);


        //defining the call
        Call<Result> call = service.removeFromFavorite(userId, dishId);

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
