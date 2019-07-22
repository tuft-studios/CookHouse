package com.atta.cookhouse.model;

import com.google.gson.annotations.SerializedName;

public class PromoCodeResult {
    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("discount")
    private int discount;

    public PromoCodeResult(Boolean error, String message, int discount) {
        this.error = error;
        this.message = message;
        this.discount = discount;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public int getDiscount() {
        return discount;
    }
}
