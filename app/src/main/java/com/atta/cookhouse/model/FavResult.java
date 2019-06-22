package com.atta.cookhouse.model;

import com.google.gson.annotations.SerializedName;

public class FavResult {

    @SerializedName("favorite")
    private Boolean favorite;

    @SerializedName("id")
    private int id;

    public int getId() {
        return id;
    }

    public FavResult(Boolean favorite, int id) {
        this.favorite = favorite;
        this.id = id;
    }

    public FavResult(Boolean favorite) {
        this.favorite = favorite;
    }

    public Boolean getFavorite() {
        return favorite;
    }
}
