package com.atta.cookhouse.model;

import java.io.Serializable;

public class Option implements Serializable {


    private String option;

    private int id ;

    public Option(String option, int id) {
        this.option = option;
        this.id = id;
    }

    public String getOption() {
        return option;
    }

    public int getId() {
        return id;
    }
}
