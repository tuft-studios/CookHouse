package com.atta.cookhouse.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class CartItem implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "dishId")
    int dishId;

    @ColumnInfo(name = "dishName")
    String dishName;

    @ColumnInfo(name = "dishPrice")
    String dishPrice;

    @ColumnInfo(name = "count")
    int count;

    @ColumnInfo(name = "kitchen")
    int kitchen;

    @ColumnInfo(name = "option")
    String option;

    @ColumnInfo(name = "side1")
    String side1;

    @ColumnInfo(name = "side2")
    String side2;


    @ColumnInfo(name = "size")
    String size;

    @ColumnInfo(name = "eta")
    int eta;

    public String getDishName() {
        return dishName;
    }

    public int getCount() {
        return count;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDishPrice() {
        return dishPrice;
    }

    public void setDishPrice(String dishPrice) {
        this.dishPrice = dishPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public void setKitchen(int kitchen) {
        this.kitchen = kitchen;
    }

    public int getKitchen() {
        return kitchen;
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

    public String getSize() {
        return size;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void setSide1(String side1) {
        this.side1 = side1;
    }

    public void setSide2(String side2) {
        this.side2 = side2;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getEta() {
        return eta;
    }

    public void setEta(int eta) {
        this.eta = eta;
    }
}
