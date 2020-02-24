package com.atta.cookhouse.setting;

import android.content.Context;

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

public class SettingPresenter implements SettingContract.Presenter {


    private SettingContract.View mView;

    private Context mContext;

    public SettingPresenter(SettingContract.View mView, Context mContext) {
        this.mView = mView;
        this.mContext = mContext;
    }

    @Override
    public void resetPassword(int userId, String oldPassword, String password) {

        //defining the call
        Call<Result> call = APIClient.getInstance().getApi().resetPassword(userId, oldPassword, password);

        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                if (response.body() != null){

                    mView.showMessage(response.body().getMessage());

                }else {
                    mView.showMessage("An error");
                }

                mView.showProgress(false);
                mView.hidePasswordPopup();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

                mView.showMessage(t.getMessage());
                mView.showProgress(false);
            }
        });
    }


    @Override
    public void updatePhone(int userId, String phone) {
/*        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIClient.BASE_URL)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);

        //Defining the user object as we need to pass it with the call
        //User user = new User(name, email, password, phone, birthdayString, locationSting);

        //defining the call
        Call<Result> call = service.updatePhone(userId, phone);

        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                if (response.body() != null){

                    mView.showMessage(response.body().getMessage());

                    SessionManager.getInstance(mContext).setPhone(response.body().getDiscount().getPhone());


                }else {
                    mView.showMessage("An error");
                }

                mView.showProgress(false);
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

                mView.showMessage(t.getMessage());
                mView.showProgress(false);
            }
        });*/
    }

    @Override
    public void sendSms(int userId, String phone, boolean forgetPwd) {


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
        Call<SmsResult> call = retrofit.create(APIService.class).sendSms("201001970483", userId);

        //calling the api
        call.enqueue(new Callback<SmsResult>() {
            @Override
            public void onResponse(Call<SmsResult> call, Response<SmsResult> response) {

                mView.showProgress(false);
                mView.hidePhonePopup();

                if (!response.body().getError()){

                    if (forgetPwd){

                        mView.showForgetPwdPopup();
                    }else {

                        mView.showCodePopup("201001970483");
                    }

                }


                mView.showMessage(response.body().getStatus());

            }

            @Override
            public void onFailure(Call<SmsResult> call, Throwable t) {

                mView.showMessage(t.getMessage());
                mView.showProgress(false);
            }
        });
    }

    @Override
    public void confirmCode(int userId, String phone, String rand) {

        //defining the call
        Call<Result> call = APIClient.getInstance().getApi().confirmPhoneCode("01001970483", userId, rand);

        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                mView.showProgress(false);
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
                mView.showProgress(false);
            }
        });
    }



    @Override
    public void confirmResetCode(int id,  String password, String rand) {

        //defining the call
        Call<Result> call = APIClient.getInstance().getApi().confirmPwdCodeById(password, id, rand);

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
