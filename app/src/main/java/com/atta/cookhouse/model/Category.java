package com.atta.cookhouse.model;

import java.io.Serializable;

public class Category implements Serializable {


    private String category;

    private int id ;

    public Category(String category, int id) {
        this.category = category;
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public int getId() {
        return id;
    }
}
