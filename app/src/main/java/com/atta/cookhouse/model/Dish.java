package com.atta.cookhouse.model;

public class Dish {


    private int dId, mediumPrice, largePrice, rating;
    private String imageUrl, dishName;

    public Dish(int dId, String dishName,int rating, int mediumPrice, int largePrice, String imageUrl) {
        this.dId = dId;
        this.dishName = dishName;
        this.rating = rating;
        this.mediumPrice = mediumPrice;
        this.largePrice = largePrice;
        this.imageUrl = imageUrl;
    }

    public int getDishId() {
        return dId;
    }

    public int getMediumPrice() {
        return mediumPrice;
    }

    public int getLargePrice() {
        return largePrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDishName() {
        return dishName;
    }

    public int getRating() {
        return rating;
    }
}
