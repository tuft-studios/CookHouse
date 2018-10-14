package com.atta.cookhouse.model;

public class User {

    private int id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String birthday;
    private String location;

    public User(String name, String email, String password, String phone, String birthday, String location) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.birthday = birthday;
        this.location = location;
    }

    public User(int id, String name, String email, String phone, String birthday, String location){
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
        this.location = location;
    }

    public User(int id, String name, String email, String password, String phone, String birthday, String location) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.birthday = birthday;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword(){
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getLocation() {
        return location;
    }
}
