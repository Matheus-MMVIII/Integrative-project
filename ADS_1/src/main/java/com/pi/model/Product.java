package com.pi.model;

public class Product {
    private final String NAME;
    private Double price;
    private Long id;
    private int stock;

    public Product(String name, Double price, int stock) {
        this.NAME = name;
        this.price = price;
        this.stock = stock;
    }

    public Product(String name, Double price, int stock, Long id) {
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

    public Long getId() {
        return id;
    }

    public int getStock() {
        return stock;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void changePrice(Double newPrice) {
        price = newPrice;
    }

    public void changeStock(int newStock) {
        stock = newStock;
    }
}
