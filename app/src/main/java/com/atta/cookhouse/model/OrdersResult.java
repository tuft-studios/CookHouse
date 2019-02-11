package com.atta.cookhouse.model;

import com.google.gson.annotations.SerializedName;

public class OrdersResult {
    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("dishes")
    private String dishes;

    public OrdersResult(Boolean error, String message, String dishes) {
        this.error = error;
        this.message = message;
        this.dishes = dishes;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
