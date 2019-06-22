package com.atta.cookhouse.setting;

public interface SettingContract {

    interface View{

        void showMessage(String message);

        void resetPassword();

        void changeLanguage(String language);

        void showPasswordPopup();

        void showLanguagePopup();

        boolean validate(String oldPassword, String newPassword, String passwordConfirm);

        void showProgress(boolean show);

        void hidePasswordPopup();

        void hidePhonePopup();

        void hideCodePopup();

        void showCodePopup(String phone);

    }

    interface Presenter{
        void resetPassword(int userId, String oldPassword, String password) ;

        void updatePhone(int userId, String phone);

        void sendSms(int UserId, String phone);

        void confirmCode(int userId, String phone, String rand);
    }
}
