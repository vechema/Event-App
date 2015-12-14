package com.aptmini.jreacs.connexus;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Andrew on 11/17/2015.
 */
public class User {
    String name;
    String number;
    HashSet<String> id = new HashSet<String>();

    private static User instance = null;

    protected User() {
        // Exists only to defeat instantiation.
    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public HashSet<String> getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void addId(String id) {
        this.id.add(id);
    }
}
