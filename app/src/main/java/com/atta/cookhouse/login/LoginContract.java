package com.atta.cookhouse.login;

public interface LoginContract {

    interface View{

        void showMessage(String error);

        void showViewError(String view ,String error);

        void showMessage();

        void navigateToMain();

        void navigateToRegister();

        void setDialog();

        void skipToMain();

        void dismissProgressDialog();

        void showPasswordPopup();

        void hidePasswordPopup();

        void hideCodePopup();

        void showCodePopup();

        void resetPassword();

        boolean validate(String code, String newPassword, String passwordConfirm);
    }

    interface Presenter{

        void login(String email, String password);


        boolean validate(String email, String password);

        void sendSms(String mobile);

        void confirmCode(String mobile, String password, String rand);
    }
}
