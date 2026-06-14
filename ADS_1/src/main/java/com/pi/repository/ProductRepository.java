package com.pi.repository;

import com.pi.model.Product;
import com.pi.utils.CsvUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private final Path path;

    public ProductRepository() {
        this.path = Path.of("ADS_1/data/Products.csv");
    }

    public void create(Product product) throws IOException {
        product.setId(nextId());
        String body = Files.readString(path);
        Files.write(path, (body + "\n" + CsvUtil.product(product)).getBytes());
    }

    public void update(int id, Product product) throws IOException {
        List<String> lines = Files.readAllLines(path);
        boolean found = false;

        for (int i = 0; i < lines.size(); i++) {
            Product current = CsvUtil.toProduct(lines.get(i));

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

    public Long nextId() throws IOException {
        long lines = Files.lines(path).count();

        if (lines <= 1)
            return 1L;

        return lines;
    }

    public List<Product> findAll() throws IOException {

        List<String> lines = Files.readAllLines(path);

        List<Product> products = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            products.add(CsvUtil.toProduct(lines.get(i)));
        }

        return products;
    }

    public Product findById(Long id) throws IOException {

        return findAll()
                .stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void delete(Long id) throws IOException {

        List<Product> products = findAll();

        StringBuilder builder = new StringBuilder();

        builder.append("id,name,price,stock\n");

        for (Product p : products) {

            if (!p.getId().equals(id)) {
                builder.append(CsvUtil.product(p))
                        .append("\n");
            }
        }

        Files.writeString(path, builder.toString());
    }
}
