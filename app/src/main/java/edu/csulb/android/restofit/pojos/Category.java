package edu.csulb.android.restofit.pojos;

import java.io.Serializable;

public class Category implements Serializable {

    public int id;
    public String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
