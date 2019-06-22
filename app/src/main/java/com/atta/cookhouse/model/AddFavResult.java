package com.atta.cookhouse.model;

import com.google.gson.annotations.SerializedName;

public class AddFavResult {

    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("id")
    private int id;

    public AddFavResult(Boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    public AddFavResult(Boolean error, String message, int id) {
        this.error = error;
        this.message = message;
        this.id = id;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }
}
