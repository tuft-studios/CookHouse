package com.atta.cookhouse.Register;

import com.atta.cookhouse.model.User;

public interface RegisterContract {

    interface View{

        void showMessage(String error);

        void showViewError(String view ,String error);

        void navigateToMain();

        void setDialog();

        boolean validate(String name, String email, String password, String passwordConfirm, String phone, String birthdayString, String locationSting, String jobSting);

    }

    interface Presenter{

        void register(User user);



    }
}
