package com.atta.cookhouse.login;

import android.content.Context;

import com.atta.cookhouse.Local.QueryUtils;
import com.atta.cookhouse.model.APIService;
import com.atta.cookhouse.model.APIUrl;
import com.atta.cookhouse.model.Result;
import com.atta.cookhouse.model.SmsResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.SMS_API_BASE_URL)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);

        //Defining the user object as we need to pass it with the call
        //User user = new User(name, email, password, phone, birthdayString, locationSting);

        //defining the call
        Call<SmsResult> call = service.sendPasswordSms(mobile);

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

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);

        //Defining the user object as we need to pass it with the call
        //User user = new User(name, email, password, password, birthdayString, locationSting);

        //defining the call
        Call<Result> call = service.confirmPasswordCode(password, mobile, rand);

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
