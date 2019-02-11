package com.atta.cookhouse.login;

import android.content.Context;

import com.atta.cookhouse.Local.QueryUtils;

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
}
