package domain.model;

import java.util.UUID;

public abstract class MenuItem {
    private String id;
    private String name;
    private double price;
    private String category;
    private String description;

    public MenuItem(String name, double price, String category, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
    }
}