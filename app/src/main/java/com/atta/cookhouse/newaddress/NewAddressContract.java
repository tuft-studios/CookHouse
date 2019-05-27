package com.atta.cookhouse.newaddress;

import com.atta.cookhouse.model.Address;

public interface NewAddressContract {

    interface View{

        void showMessage(String message);

        void initiateViews();

        void setStreet(String street);

        void setArea(String area);

        void setFullAddress(String formattedAddress);

        void setBuildingNumber(String buildingNumber);

        void setCity(String cityName);

        void moveToAddressesActivity();
    }

    interface Presenter{

        void getAddress(String url);

        void addAddress(Address address);

        void editAddress(Address address);

        void deleteAddress(int id);
    }
}
