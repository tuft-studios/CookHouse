package com.atta.cookhouse.profile;

import android.content.Context;

import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.Address;
import com.atta.cookhouse.model.Profile;
import com.atta.cookhouse.model.Result;
import com.atta.cookhouse.model.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePresenter implements ProfileContract.Presenter {

    private ProfileContract.View mView;

    private Context mContext;

    public ProfilePresenter(ProfileContract.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    @Override
    public void getProfile(int userId) {

        //defining the call
        Call<Profile> call = APIClient.getInstance().getApi().getProfile(userId);

        //calling the api
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {

                if (response.body() != null){
                    if (!response.body().getError()){

                        User user = response.body().getUser();

                        mView.showProfile(user);

                        if (!response.body().getAddressesError()){

                            ArrayList<Address> addresses = response.body().getAddresses();

                            ArrayList<String> addressesNames= new ArrayList<>();


                            if (addresses.size() > 0){

                                for (int i = 0; i < addresses.size(); i++){
                                    addressesNames.add(addresses.get(i).getAddressName());
                                }
                            }

                            mView.showAddresses(addresses, addressesNames);
                        }else {

                            String error = response.body().getErrorMsg();
                            mView.showAddressesMessage(error);

                        }


                    }
                }else {
                    mView.showMessage("An error");
                }

            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {

                mView.showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void updateProfile(User user, int add_id) {

        //defining the call
        Call<Result> call = APIClient.getInstance().getApi().updateProfile(
                user.getId(),
                user.getName(),
                add_id,
                user.getBirthday(),
                user.getJob(),
                user.getLocation()
        );

        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                //displaying the message from the response as toast
                if (response.body() != null) {


                    mView.showMessage(response.body().getMessage());


                    if (!response.body().getError()){


                        mView.moveToMain();
                    }


                }else {
                    mView.showMessage("An error");
                }

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

                mView.showMessage(t.getMessage());
            }
        });
    }


}
