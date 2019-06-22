package com.atta.cookhouse.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Profile {

    @SerializedName("error")
    private Boolean error;


    @SerializedName("user")
    private User user;


    @SerializedName("addresses_error")
    private Boolean addressesError;


    @SerializedName("error_msg")
    private String errorMsg;

    @SerializedName("addresses")
    private ArrayList<Address> addresses;

    public Profile() {
    }

    public User getUser() {
        return user;
    }

    public Boolean getAddressesError() {
        return addressesError;
    }

    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public Boolean getError() {
        return error;
    }


    public String getErrorMsg() {
        return errorMsg;
    }
}
