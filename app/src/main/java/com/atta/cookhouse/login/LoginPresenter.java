package com.atta.cookhouse.login;

import android.content.Context;

import com.atta.cookhouse.Local.QueryUtils;
import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.Result;
import com.atta.cookhouse.model.SmsResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter implements LoginContract.Presenter{

    private LoginContract.View mView;

    private Context mContext;

    public LoginPresenter(LoginContract.View view, Context context) {

        mView = view;

        mContext = context;
    }

    @Override
    public void login(String email, String password) {


        QueryUtils.login( email,  password, mView, null, mContext);

    }

    @Override
    public boolean validate(String email, String password) {


        return QueryUtils.validate( email,  password, mView, null, mContext);
    }


    @Override
    public void sendSms(String mobile) {

        //defining the call
        Call<SmsResult> call = APIClient.getInstance().getApi().sendPasswordSms(mobile);

        //calling the api
        call.enqueue(new Callback<SmsResult>() {
            @Override
            public void onResponse(Call<SmsResult> call, Response<SmsResult> response) {

                mView.hidePasswordPopup();

                if (!response.body().getError()){

                    mView.showError(response.body().getStatus());

                    mView.showCodePopup();

                }else {
                    mView.showError("An error");
                }

            }

            @Override
            public void onFailure(Call<SmsResult> call, Throwable t) {

                mView.showError(t.getMessage());
            }
        });
    }

    @Override
    public void confirmCode(String mobile, String password, String rand) {

        //defining the call
        Call<Result> call = APIClient.getInstance().getApi().confirmPasswordCode(password, mobile, rand);

        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                mView.hideCodePopup();

                if (!response.body().getError()){

                    mView.showError(response.body().getMessage());

                }else {
                    mView.showError("An error");
                }

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

                mView.showError(t.getMessage());
            }
        });
    }
}
