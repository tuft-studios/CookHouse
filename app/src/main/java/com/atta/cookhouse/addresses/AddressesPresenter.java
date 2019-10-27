package com.atta.cookhouse.addresses;

import android.content.Context;

import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.Address;
import com.atta.cookhouse.model.Addresses;
import com.atta.cookhouse.model.Result;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressesPresenter implements AddressesContract.Presenter {

    private AddressesContract.View mView;

    private Context mContext;

    public AddressesPresenter(AddressesContract.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    @Override
    public void getAddresses(int userId) {

        //defining the call
        Call<Addresses> call = APIClient.getInstance().getApi().getAddresses(userId);

        //calling the api
        call.enqueue(new Callback<Addresses>() {
            @Override
            public void onResponse(Call<Addresses> call, Response<Addresses> response) {

                if (response.body() != null){
                    if (response.body().getAddresses() != null){

                        ArrayList<Address> addresses = response.body().getAddresses();

                        if (addresses.size() > 0){

                            mView.showRecyclerView(addresses);
                        }else {

                            mView.updateText();
                        }

                    }
                }else {
                    mView.showMessage("An error");
                }

            }

            @Override
            public void onFailure(Call<Addresses> call, Throwable t) {

                mView.showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void removeAddresses(int id) {

        //defining the call
        Call<Result> call = APIClient.getInstance().getApi().removeAddress(id);

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
