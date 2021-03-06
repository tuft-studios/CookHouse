package com.atta.cookhouse.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OrdersResult {
    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("dishes")
    private ArrayList<Dish> dishes;


    @SerializedName("order_kitchens")
    private ArrayList<Integer> order_kitchens;

    @SerializedName("address")
    private Address address;

    public ArrayList<Integer> getOrder_kitchens() {
        return order_kitchens;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Dish> getDishes() {
        return dishes;
    }

    public Address getAddress() {
        return address;
    }

}
