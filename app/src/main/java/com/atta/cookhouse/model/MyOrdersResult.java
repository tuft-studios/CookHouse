package com.atta.cookhouse.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MyOrdersResult {

    @SerializedName("error")
    private Boolean error;

    @SerializedName("orders")
    private ArrayList<Order> orders;

    public MyOrdersResult(Boolean error, ArrayList<Order> orders) {
        this.error = error;
        this.orders = orders;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public Boolean getError() {
        return error;
    }
}
