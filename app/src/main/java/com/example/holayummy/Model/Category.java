package com.example.holayummy.Model;

public class Category {
    private String Name;
    private String Imgage;

    public Category() {
    }

    public Category(String name, String imgage) {
        Name = name;
        Imgage = imgage;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImgage() {
        return Imgage;
    }

    public void setImgage(String imgage) {
        Imgage = imgage;
    }
}
