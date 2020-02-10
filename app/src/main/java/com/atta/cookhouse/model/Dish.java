package com.atta.cookhouse.model;

import java.io.Serializable;

public class Dish implements Serializable {


    private int dId, eta, size, quantity, cartCount, likes;
    private double priceM, priceL, costM, costL;
    private String imageUrl, dishName, dishDisc, dishNameArabic, dishDiscArabic, location,
            options, sides1, sides2, selectedOption, selectedSide1, selectedSide2, selectedSize,
            option, side1, side2;
    private boolean isFav;

    private String dishSize;


    public String getSelectedOption() {
        return selectedOption;
    }

    public String getSelectedSide1() {
        return selectedSide1;
    }

    public String getSelectedSide2() {
        return selectedSide2;
    }

    public String getSelectedSize() {
        return selectedSize;
    }

    public void setPriceM(int priceM) {
        this.priceM = priceM;
    }

    public void setPriceL(int priceL) {
        this.priceL = priceL;
    }

    public void setCostM(int costM) {
        this.costM = costM;
    }

    public void setCostL(int costL) {
        this.costL = costL;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public void setDishDisc(String dishDisc) {
        this.dishDisc = dishDisc;
    }

    public void setDishNameArabic(String dishNameArabic) {
        this.dishNameArabic = dishNameArabic;
    }

    public void setDishDiscArabic(String dishDiscArabic) {
        this.dishDiscArabic = dishDiscArabic;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public void setSides1(String sides1) {
        this.sides1 = sides1;
    }

    public void setSides2(String sides2) {
        this.sides2 = sides2;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public void setSelectedSide1(String selectedSide1) {
        this.selectedSide1 = selectedSide1;
    }

    public void setSelectedSide2(String selectedSide2) {
        this.selectedSide2 = selectedSide2;
    }

    public void setSelectedSize(String selectedSize) {
        this.selectedSize = selectedSize;
    }

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

    public Dish(int dId, int size, int priceM, int priceL, int costM, int eta, int likes,
                int quantity, int cartCount, String imageUrl, String dishName, String dishDisc,
                String dishNameArabic, String dishDiscArabic, String location, boolean isFav) {
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

    public Dish(int dId, int priceM, int priceL, int quantity, String dishName, String option,
                String side1, String side2, String dishSize) {
        this.dId = dId;
        this.dishSize = dishSize;
        this.priceM = priceM;
        this.priceL = priceL;
        this.quantity = quantity;
        this.dishName = dishName;
        this.option = option;
        this.side1 = side1;
        this.side2 = side2;
    }

    public int getDishId() {
        return dId;
    }

    public double getPriceM() {
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

    public double getPriceL() {
        return priceL;
    }

    public double getCostM() {
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

    public double getCostL() {
        return costL;
    }

    public String getSides2() {
        return sides2;
    }

    public String getSishSize() {
        return dishSize;
    }

    public String getOption() {
        return option;
    }

    public String getSide1() {
        return side1;
    }

    public String getSide2() {
        return side2;
    }
}
