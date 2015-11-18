package com.aptmini.jreacs.connexus;

/**
 * Created by Andrew on 11/17/2015.
 */
public class User {
    String name;
    String number;

    private static User instance = null;
    protected User() {
        // Exists only to defeat instantiation.
    }
    public static User getInstance() {
        if(instance == null) {
            instance = new User();
        }
        return instance;
    }
    public String getName(){
        return name;
    }

    public String getNumber(){
        return number;
    }
    public void setName(String name){
        this.name = name;
    }

    public void setNumber(String number){
        this.number = number;
    }
}
