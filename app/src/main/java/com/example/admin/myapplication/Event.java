package com.example.admin.myapplication;

/**
 * Created by admin on 12/19/2017.
 */

public class Event {
    private int id;
    private String title;
    private String description;
    private double price;
    private String startTime;
    // TODO the rest


    public Event(int id, String title, String description, double price, String startTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getStartTime() { return startTime; }
}
