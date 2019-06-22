package com.atta.cookhouse.model;

import com.google.gson.annotations.SerializedName;

public class DishResult {

    @SerializedName("dish")
    private Dish dish;

    public Dish getDish() {
        return dish;
    }
}
