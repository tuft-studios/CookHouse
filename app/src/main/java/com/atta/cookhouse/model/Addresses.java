package com.atta.cookhouse.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Addresses {

    @SerializedName("error")
    private Boolean error;

    @SerializedName("error_msg")
    private String message;

    @SerializedName("addresses")
    private ArrayList<Address> addresses;

    public Addresses() {
    }

    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
