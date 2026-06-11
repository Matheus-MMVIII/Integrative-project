package com.pi.model;

public class Product {
    private final String NAME;
    private Double price;
    private final int ID;
    private int stock;

    public Product(String name, Double price, int id, int stock) {
        this.NAME = name;
        this.price = price;
        this.ID = id;
        this.stock = stock;
    }

    public String getName() {
        return NAME;
    }

    public Double getPrice() {
        return price;
    }

    public int getId() {
        return ID;
    }

    public int getStock() {
        return stock;
    }

    public void changePrice(Double newPrice) {
        price = newPrice;
    }

    public void changeStock(int newStock) {
        stock = newStock;
    }
}
