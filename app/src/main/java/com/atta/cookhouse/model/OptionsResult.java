package com.atta.cookhouse.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OptionsResult {

    @SerializedName("error")
    private Boolean error;

    @SerializedName("options")
    private ArrayList<Option> options;

    public OptionsResult(Boolean error, ArrayList<Option> options) {
        this.error = error;
        this.options = options;
    }

    public Boolean getError() {
        return error;
    }

    public ArrayList<Option> getOptions() {
        return options;
    }
}
