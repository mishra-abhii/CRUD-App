package com.abhishek.crud_app;

public class Items {
    String itemId, title, description;

    public Items(String itemId, String title, String description) {
        this.itemId = itemId;
        this.title = title;
        this.description = description;
    }

    public Items(){}  // empty constructor for firebase as it is used while reading the values

    public String getItemId() {
        return itemId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}

