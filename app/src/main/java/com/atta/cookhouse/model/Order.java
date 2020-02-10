package com.atta.cookhouse.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {

    @SerializedName("order_dishes")
    private ArrayList<Dish> dishesList;


    private int orderId, userId, addressId, status, eta, points;
    private double subtotal, delivery, total, discount;
    private String location, schedule, orderTime, creationTime, phone, fullAddress, addressName,
            dishes, count, options, sizes, sides1, sides2, comment, promocode;

    public Order(int orderId, ArrayList<Dish> dishesList, int status, double subtotal, double delivery, double total, double discount,
                 String phone, String orderTime, String creationTime, String fullAddress, String addressName, int eta,
                 String comment) {
        this.dishesList = dishesList;
        this.orderId = orderId;
        this.status = status;
        this.subtotal = subtotal;
        this.delivery = delivery;
        this.total = total;
        this.discount = discount;
        this.phone = phone;
        this.orderTime = orderTime;
        this.creationTime = creationTime;
        this.fullAddress = fullAddress;
        this.addressName = addressName;
        this.comment = comment;
        this.eta = eta;
    }

    public Order(String dishes, String count, String options, String sizes, String sides1, String sides2,
                 double subtotal, double delivery, double total, double discount, int userId, String location,
                 int addressId, String phone, String orderTime, String creationTime, int eta, String comment, String promocode, int points) {
        this.dishes = dishes;
        this.count = count;
        this.options = options;
        this.sizes = sizes;
        this.sides1 = sides1;
        this.sides2 = sides2;
        this.subtotal = subtotal;
        this.delivery = delivery;
        this.total = total;
        this.discount = discount;
        this.userId = userId;
        this.location = location;
        this.addressId = addressId;
        this.phone = phone;
        this.orderTime = orderTime;
        this.creationTime = creationTime;
        this.eta = eta;
        this.comment = comment;
        this.promocode = promocode;
        this.points = points;
    }

    public String getSchedule() {


        return schedule;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public String getDishes() {

        return dishes;
    }

    public String getCount() {
        return count;
    }

    public double getSubtotalPrice() {
        return subtotal;
    }

    public double getDelivery() {
        return delivery;
    }

    public double getTotalPrice() {
        return total;
    }

    public double getDiscount() {
        return discount;
    }

    public String getLocation() {
        return location;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {

        return userId;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public void setCreationTime(String creationTime) {
        creationTime = creationTime;
    }

    public String getCreationTime() {

        return creationTime;
    }

    public String getMobile() {
        return phone;
    }

    public ArrayList<Dish> getDishesList() {
        return dishesList;
    }

    public String getFullAddress() {
        return fullAddress;
    }


    public int getId() {
        return orderId;
    }


    public int getStatus() {
        return status;
    }


    public String getOptions() {
        return options;
    }

    public String getSizes() {
        return sizes;
    }

    public String getSides1() {
        return sides1;
    }

    public String getSides2() {
        return sides2;
    }

    public String getComment() {
        return comment;
    }

    public int getEta() {
        return eta;
    }

    public String getAddressName() {
        return addressName;
    }

    public int getPoints() {
        return points;
    }

    public String getPromocode() {
        return promocode;
    }
}
