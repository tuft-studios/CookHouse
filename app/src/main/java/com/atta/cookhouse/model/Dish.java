package com.atta.cookhouse.model;

import java.io.Serializable;

public class Dish implements Serializable {


    private int dId, priceM, priceL, costM, costL, eta, size, quantity, cartCount, likes;
    private String imageUrl, dishName, dishDisc, dishNameArabic, dishDiscArabic, location, options, sides1, sides2;
    boolean isFav;


    public Dish(int dId, String dishName, String dishDisc, int size, int priceM, int priceL, int costM, int eta, String imageUrl, String location, int likes) {
        this.dId = dId;
        this.dishName = dishName;
        this.dishDisc = dishDisc;
        this.size = size;
        this.priceM = priceM;
        this.priceL = priceL;
        this.costM = costM;
        this.eta = eta;
        this.imageUrl = imageUrl;
        this.location = location;
        this.likes = likes;
    }

    public Dish(int id, int quantity) {
        this.dId = id;
        this.quantity = quantity;
    }

    public Dish(int dId, int size, int priceM, int priceL, int costM, int eta, int likes, int cartCount, String imageUrl, String dishName, String dishDisc, String location, boolean isFav) {
        this.dId = dId;
        this.size = size;
        this.priceM = priceM;
        this.priceL = priceL;
        this.costM = costM;
        this.eta = eta;
        this.likes = likes;
        this.cartCount = cartCount;
        this.imageUrl = imageUrl;
        this.dishName = dishName;
        this.dishDisc = dishDisc;
        this.location = location;
        this.isFav = isFav;
    }

    public Dish(int dId, int size, int priceM, int priceL, int costM, int eta, int likes, int quantity, int cartCount, String imageUrl, String dishName, String dishDisc, String dishNameArabic, String dishDiscArabic, String location, boolean isFav) {
        this.dId = dId;
        this.size = size;
        this.priceM = priceM;
        this.priceL = priceL;
        this.costM = costM;
        this.eta = eta;
        this.likes = likes;
        this.quantity = quantity;
        this.cartCount = cartCount;
        this.imageUrl = imageUrl;
        this.dishName = dishName;
        this.dishDisc = dishDisc;
        this.dishNameArabic = dishNameArabic;
        this.dishDiscArabic = dishDiscArabic;
        this.location = location;
        this.isFav = isFav;
    }

    public int getDishId() {
        return dId;
    }

    public int getPriceM() {
        return priceM;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDishName() {
        return dishName;
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

    public int getQuantity() {
        return quantity;
    }

    public int getCartCount() {
        return cartCount;
    }

    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public String getDishNameArabic() {
        return dishNameArabic;
    }

    public String getDishDiscArabic() {
        return dishDiscArabic;
    }

    public int getPriceL() {
        return priceL;
    }

    public int getCostM() {
        return costM;
    }

    public int getEta() {
        return eta;
    }

    public int getSize() {
        return size;
    }

    public String getOptions() {
        return options;
    }

    public String getSides1() {
        return sides1;
    }

    public int getCostL() {
        return costL;
    }

    public String getSides2() {
        return sides2;
    }
}
