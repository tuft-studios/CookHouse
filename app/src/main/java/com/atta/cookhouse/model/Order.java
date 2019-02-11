package com.atta.cookhouse.model;

import java.util.Map;

public class Order {


    private Map<String, String> dishes, count ;
    private int subtotalPrice, delivery, totalPrice, discount, userId, kitchen;
    private String location, address, schedule, orderTime, CreationTime;

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public Order(Map<String, String> dishes, Map<String, String> count, int subtotalPrice, int delivery, int totalPrice, int discount, int userId, String location, int kitchen, String address, String schedule, String orderTime, String creationTime) {
        this.dishes = dishes;
        this.count = count;
        this.subtotalPrice = subtotalPrice;
        this.delivery = delivery;
        this.totalPrice = totalPrice;
        this.discount = discount;
        this.userId = userId;
        this.location = location;
        this.kitchen = kitchen;
        this.address = address;
        this.schedule = schedule;
        this.orderTime = orderTime;
        CreationTime = creationTime;
    }

    public String getSchedule() {


        return schedule;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setDishes(Map<String, String> dishes) {
        this.dishes = dishes;
    }

    public void setCount(Map<String, String> count) {
        this.count = count;
    }

    public void setSubtotalPrice(int subtotalPrice) {
        this.subtotalPrice = subtotalPrice;
    }

    public void setDelivery(int delivery) {
        this.delivery = delivery;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, String> getDishes() {

        return dishes;
    }

    public Map<String, String> getCount() {
        return count;
    }

    public int getSubtotalPrice() {
        return subtotalPrice;
    }

    public int getDelivery() {
        return delivery;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public int getDiscount() {
        return discount;
    }

    public String getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {

        return userId;
    }

    public void setCreationTime(String creationTime) {
        CreationTime = creationTime;
    }

    public String getCreationTime() {

        return CreationTime;
    }

    public int getKitchen() {
        return kitchen;
    }
}
