package com.atta.cookhouse.login;

public interface LoginContract {

    interface View{

        void showError(String error);

        void showViewError(String view ,String error);

        void showMessage();

        void navigateToMain();

        void navigateToRegister();

        void setDialog();
    }

    interface Presenter{

        void login(String email, String password);


        boolean validate(String email, String password);
    }
}
