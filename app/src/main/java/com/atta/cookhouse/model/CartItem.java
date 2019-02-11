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
}
