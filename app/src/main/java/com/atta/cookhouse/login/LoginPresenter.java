package com.atta.cookhouse.login;

import android.content.Context;

import com.atta.cookhouse.Local.QueryUtils;
import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.APIService;
import com.atta.cookhouse.model.Result;
import com.atta.cookhouse.model.SmsResult;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://52.15.188.41/cookhouse/nexmo/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        //defining the call
        Call<SmsResult> call = retrofit.create(APIService.class).sendPasswordSms(mobile);

        //calling the api
        call.enqueue(new Callback<SmsResult>() {
            @Override
            public void onResponse(Call<SmsResult> call, Response<SmsResult> response) {

                mView.hidePasswordPopup();

                assert response.body() != null;
                if (!response.body().getError()){

                    mView.showCodePopup();

                }


                mView.showMessage(response.body().getStatus());

            }

            @Override
            public void onFailure(Call<SmsResult> call, Throwable t) {

                mView.showMessage(t.getMessage());
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

                    mView.showMessage(response.body().getMessage());

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
