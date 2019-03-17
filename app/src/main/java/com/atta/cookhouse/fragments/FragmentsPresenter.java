package com.atta.cookhouse.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.andremion.counterfab.CounterFab;
import com.atta.cookhouse.Local.QueryUtils;
import com.atta.cookhouse.model.APIService;
import com.atta.cookhouse.model.APIUrl;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.Dishes;

import java.util.ArrayList;

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
    public void getMenu(final RecyclerView recyclerView, String type, String location) {

        ArrayList<Dish> dishes = null;


        //building retrofit object
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);

        //Defining the user object as we need to pass it with the call
        //User user = new User(name, email, password, phone, birthdayString, locationSting);

        //defining the call
        retrofit2.Call<Dishes> call = service.getMenu(type, location);

        //calling the api
        call.enqueue(new retrofit2.Callback<Dishes>() {
            @Override
            public void onResponse(retrofit2.Call<Dishes> call, retrofit2.Response<Dishes> response) {

                if (response.body() != null){
                    if (response.body().getDishes() != null){

                        ArrayList<Dish> dishes = response.body().getDishes();

                        if (dishes.size() > 0){

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
    public void getCartItem(final int id, final String dishName, final String price, final int count, final int kitchen) {

        QueryUtils.getCartItem(id, dishName, price, count, kitchen, mContext, mCounterFab);


    }




}
