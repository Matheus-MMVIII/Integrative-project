package com.pi;

import com.pi.model.Administrator;
import com.pi.model.Login;
import com.pi.model.Product;
import com.pi.repository.ProductRepository;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        Administrator administrator = new Administrator("admin@email.com", "123456", new ProductRepository());
        administrator.createProduct(new Product("Test", 20.0d, 10));
    }
}