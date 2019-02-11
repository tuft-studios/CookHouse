package com.atta.cookhouse.model;

public class Dish {


    private int dId, price, kitchen, likes;
    private String imageUrl, dishName, dishDisc, location;


    public Dish(int dId, String dishName, String dishDisc, int kitchen, int price, String imageUrl, String location, int likes) {
        this.dId = dId;
        this.dishName = dishName;
        this.dishDisc = dishDisc;
        this.kitchen = kitchen;
        this.price = price;
        this.imageUrl = imageUrl;
        this.location = location;
        this.likes = likes;
    }

    public int getDishId() {
        return dId;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDishName() {
        return dishName;
    }

    public int getKitchen() {
        return kitchen;
    }

    public String getDishDisc() {
        return dishDisc;
    }

    public String getLocation() {
        return location;
    }

    public int getLikes() {
        return likes;
    }
}
