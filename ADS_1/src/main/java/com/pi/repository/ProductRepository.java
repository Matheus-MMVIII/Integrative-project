package com.pi.repository;

import com.pi.model.Product;
import com.pi.utils.CsvUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProductRepository {
    private final Path path;

    public ProductRepository() {
        this.path = Path.of("ADS_1/data/Products.csv");
    }

    public void createProduct(Product product) throws IOException {
        String body = Files.readString(path);
        Files.write(path, (body + "\n" + CsvUtil.product(product)).getBytes());
    }
}
