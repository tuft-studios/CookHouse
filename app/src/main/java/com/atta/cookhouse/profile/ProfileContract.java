package com.atta.cookhouse.profile;

import com.atta.cookhouse.model.Address;
import com.atta.cookhouse.model.User;

import java.util.List;

public interface ProfileContract {

    interface View{

        void showAddressesMessage(String message);

        void showAddresses(List<Address> mAddresses, List<String> mAddressesArray);

        void showProfile(User user);

        void showMessage(String message);
    }

    interface Presenter{

        void getProfile(int userId) ;

        void updateProfile(int userId) ;

    }
}
