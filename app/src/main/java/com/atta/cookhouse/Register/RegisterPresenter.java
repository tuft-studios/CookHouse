package com.atta.cookhouse.Register;

import android.app.ProgressDialog;
import android.content.Context;

import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.Result;
import com.atta.cookhouse.model.SessionManager;
import com.atta.cookhouse.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterPresenter implements RegisterContract.Presenter{

    private RegisterContract.View mView;

    private ProgressDialog mProgressDialog;

    private Context mContext;


    public RegisterPresenter(RegisterContract.View view, ProgressDialog progressDialog, Context context) {

        mView = view;

        mProgressDialog = progressDialog;

        mContext = context;
    }

    @Override
    public void register(User user) {

        //defining the call
        Call<Result> call = APIClient.getInstance().getApi().createUser(
                user.getName(),
                user.getEmail(),
                user.getJob(),
                user.getPassword(),
                user.getPhone(),
                user.getBirthday(),
                user.getLocation()
        );

        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                //hiding progress dialog
                if(mProgressDialog != null || mProgressDialog.isShowing() ){
                    mProgressDialog.dismiss();
                }

                //displaying the message from the response as toast
                mView.showMessage(response.body().getMessage());
                //if there is no error
                if (!response.body().getError()) {
                    //starting profile activity
                    SessionManager.getInstance(mContext).setLanguage(SessionManager.getInstance(mContext).getLanguage());
                    SessionManager.getInstance(mContext).createLoginSession(response.body().getUser());
                    mView.navigateToMain();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                if(mProgressDialog != null || mProgressDialog.isShowing() ){
                    mProgressDialog.dismiss();
                }
                mView.showMessage(t.getMessage());
            }
        });
    }

}
