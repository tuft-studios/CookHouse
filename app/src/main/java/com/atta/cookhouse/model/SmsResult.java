package com.atta.cookhouse.model;

import com.google.gson.annotations.SerializedName;

public class SmsResult {
    @SerializedName("error")
    private Boolean error;

    @SerializedName("status")
    private String status;

    @SerializedName("id")
    private String id;

    public SmsResult(Boolean error, String status, String id) {
        this.error = error;
        this.status = status;
        this.id = id;
    }

    public Boolean getError() {
        return error;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }
}
