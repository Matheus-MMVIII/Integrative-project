package com.pi.repository;

import com.pi.model.Product;
import com.pi.utils.CsvUtil;
import com.pi.utils.DataFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private static final String HEADER = "id,name,price,stock";
    private final Path path;

    public ProductRepository() throws IOException {
        path = DataFiles.data("Products.csv");
        DataFiles.ensure(path, HEADER);
    }

    public synchronized Product create(Product product) throws IOException {
        if (findAll().stream().anyMatch(p -> p.getName().equalsIgnoreCase(product.getName()))) {
            throw new IllegalArgumentException("Ja existe um produto com esse nome.");
        }
        product.setId(DataFiles.nextId(findAll().stream().map(Product::getId).toList()));
        List<String> lines = Files.readAllLines(path);
        lines.add(toCsv(product));
        DataFiles.writeAtomically(path, lines);
        return product;
    }

    public synchronized void update(long id, Product product) throws IOException {
        List<Product> products = findAll();
        Product current = products.stream().filter(p -> p.getId() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));
        current.setName(product.getName());
        current.setPrice(product.getPrice());
        current.setStock(product.getStock());
        saveAll(products);
    }

    public synchronized List<Product> findAll() throws IOException {
        List<Product> products = new ArrayList<>();
        List<String> lines = Files.readAllLines(path);
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                products.add(fromCsv(lines.get(i)));
            }
        }
        return products;
    }

    public synchronized Product findById(Long id) throws IOException {
        return findAll().stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    public synchronized List<Product> search(String term) throws IOException {
        String normalized = term == null ? "" : term.toLowerCase();
        return findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(normalized)
                        || p.getId().toString().equals(normalized))
                .toList();
    }

    public synchronized void changeStock(long id, int quantityDelta) throws IOException {
        List<Product> products = findAll();
        Product product = products.stream().filter(p -> p.getId() == id).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));
        product.setStock(product.getStock() + quantityDelta);
        saveAll(products);
    }

    public synchronized void delete(Long id) throws IOException {
        List<Product> products = findAll();
        if (!products.removeIf(product -> product.getId().equals(id))) {
            throw new IllegalArgumentException("Produto nao encontrado.");
        }
        saveAll(products);
    }

    private void saveAll(List<Product> products) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        products.forEach(product -> lines.add(toCsv(product)));
        DataFiles.writeAtomically(path, lines);
    }

    private String toCsv(Product product) {
        return CsvUtil.line(product.getId(), product.getName(), product.getPrice(), product.getStock());
    }

    private Product fromCsv(String csv) {
        List<String> data = CsvUtil.parse(csv);
        return new Product(Long.parseLong(data.get(0)), data.get(1),
                Double.parseDouble(data.get(2)), Integer.parseInt(data.get(3)));
    }
}
