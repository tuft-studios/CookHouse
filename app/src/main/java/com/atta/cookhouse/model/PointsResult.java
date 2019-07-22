package com.atta.cookhouse.model;

import com.google.gson.annotations.SerializedName;

public class PointsResult {
    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("points")
    private int points;

    public PointsResult(Boolean error, String message, int upointsser) {
        this.error = error;
        this.message = message;
        this.points = points;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public int getPoints() {
        return points;
    }
}
