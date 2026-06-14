package com.pi.model;

public final class Product {
    private Long id;
    private String name;
    private double price;
    private int stock;

    public Product(String name, Double price, int stock) {
        this(null, name, price, stock);
    }

    public Product(Long id, String name, double price, int stock) {
        setId(id);
        setName(name);
        setPrice(price);
        setStock(stock);
    }

    public Product(String name, Double price, int stock, Long id) {
        this(id, name, price, stock);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O nome do produto e obrigatorio.");
        }
        this.name = name.trim();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("O preco nao pode ser negativo.");
        }
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("O estoque nao pode ser negativo.");
        }
        this.stock = stock;
    }

    public void changePrice(Double newPrice) {
        setPrice(newPrice);
    }

    public void changeStock(int newStock) {
        setStock(newStock);
    }
}
