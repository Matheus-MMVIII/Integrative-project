package com.pi.repository;

import com.pi.model.Supplier;
import com.pi.utils.CsvUtil;
import com.pi.utils.DataFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SupplierRepository {
    private static final String HEADER = "id,name,cnpj,phone,email";
    private final Path path;

    public SupplierRepository() throws IOException {
        path = DataFiles.data("Suppliers.csv");
        DataFiles.ensure(path, HEADER);
    }

    public synchronized Supplier create(Supplier supplier) throws IOException {
        if (findAll().stream().anyMatch(current -> current.getCnpj().equals(supplier.getCnpj()))) {
            throw new IllegalArgumentException("CNPJ ja cadastrado.");
        }
        supplier.setId(DataFiles.nextId(findAll().stream().map(Supplier::getId).toList()));
        List<String> lines = Files.readAllLines(path);
        lines.add(toCsv(supplier));
        DataFiles.writeAtomically(path, lines);
        return supplier;
    }

    public synchronized List<Supplier> findAll() throws IOException {
        List<Supplier> suppliers = new ArrayList<>();
        List<String> lines = Files.readAllLines(path);
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                suppliers.add(fromCsv(lines.get(i)));
            }
        }
        return suppliers;
    }

    public synchronized Supplier findById(long id) throws IOException {
        return findAll().stream().filter(supplier -> supplier.getId() == id).findFirst().orElse(null);
    }

    public synchronized void update(Supplier updated) throws IOException {
        List<Supplier> suppliers = findAll();
        Supplier current = suppliers.stream().filter(supplier -> supplier.getId().equals(updated.getId())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor nao encontrado."));
        boolean duplicated = suppliers.stream().anyMatch(supplier ->
                !supplier.getId().equals(updated.getId()) && supplier.getCnpj().equals(updated.getCnpj()));
        if (duplicated) {
            throw new IllegalArgumentException("CNPJ ja cadastrado.");
        }
        current.setName(updated.getName());
        current.setCnpj(updated.getCnpj());
        current.setPhone(updated.getPhone());
        current.setEmail(updated.getEmail());
        saveAll(suppliers);
    }

    public synchronized void delete(long id) throws IOException {
        List<Supplier> suppliers = findAll();
        if (!suppliers.removeIf(supplier -> supplier.getId() == id)) {
            throw new IllegalArgumentException("Fornecedor nao encontrado.");
        }
        saveAll(suppliers);
    }

    private void saveAll(List<Supplier> suppliers) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        suppliers.forEach(supplier -> lines.add(toCsv(supplier)));
        DataFiles.writeAtomically(path, lines);
    }

    private String toCsv(Supplier supplier) {
        return CsvUtil.line(supplier.getId(), supplier.getName(), supplier.getCnpj(),
                supplier.getPhone(), supplier.getEmail());
    }

    private Supplier fromCsv(String csv) {
        List<String> data = CsvUtil.parse(csv);
        return new Supplier(Long.parseLong(data.get(0)), data.get(1), data.get(2), data.get(3), data.get(4));
    }
}
