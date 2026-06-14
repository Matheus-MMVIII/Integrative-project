package com.pi.repository;

import com.pi.model.Product;
import com.pi.utils.CsvUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ProductRepository {
    private final Path path;

    public ProductRepository() {
        this.path = Path.of("ADS_1/data/Products.csv");
    }

    public void create(Product product) throws IOException {
        String body = Files.readString(path);
        Files.write(path, (body + "\n" + CsvUtil.product(product)).getBytes());
    }

    public void update(int id, Product product) throws IOException {
        List<String> lines = Files.readAllLines(path);
        boolean found = false;

        for (int i = 0; i < lines.size(); i++) {
            Product current = CsvUtil.buildProduct(lines.get(i));

            if (current.getId() == id) {
                lines.set(i, CsvUtil.product(product));
                break;
            }
        }

        if (!found) {
            throw new RuntimeException("Product not found");
        }

        Files.write(path, lines);
    }
}
