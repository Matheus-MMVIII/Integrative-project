package com.pi.model;

import com.pi.repository.ProductRepository;

import java.io.IOException;

public class Administrator {
    private ProductRepository productRepository;

    public Administrator(String email, String password, ProductRepository productRepository) throws IOException {
        Login login = new Login(email, password);
        if (!login.getType().equals("ADMIN"))
            throw new IllegalArgumentException("Invalid type. ");
        this.productRepository = productRepository;
    }

    public void createProduct(Product product) throws IOException {
        productRepository.createProduct(product);
    }
}
