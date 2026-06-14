package com.pi.model;

public class Product {
    private final String NAME;
    private Double price;
    private int id;
    private int stock;

    public Product(String name, Double price, int stock) {
        this.NAME = name;
        this.price = price;
        this.stock = stock;
    }

    public Product(String name, Double price, int stock, int id) {
        this.NAME = name;
        this.price = price;
        this.stock = stock;
        this.id = id;
    }

    public String getName() {
        return NAME;
    }

    public Double getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    public int getStock() {
        return stock;
    }

    public void seId(int id) {
        this.id = id;
    }

    public void changePrice(Double newPrice) {
        price = newPrice;
    }

    public void changeStock(int newStock) {
        stock = newStock;
    }
}
