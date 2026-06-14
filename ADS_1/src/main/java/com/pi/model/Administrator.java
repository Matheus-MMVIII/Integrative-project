package com.pi.model;

import com.pi.repository.ProductRepository;

import java.io.IOException;

public class Administrator {
    private ProductRepository productRepository;

    public Administrator(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(Product product) throws IOException {
        productRepository.create(product);
    }
}
