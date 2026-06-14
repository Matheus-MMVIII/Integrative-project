package com.pi.repository;

import com.pi.model.PaymentMethod;
import com.pi.model.Sale;
import com.pi.model.SaleItem;
import com.pi.model.SaleStatus;
import com.pi.utils.CsvUtil;
import com.pi.utils.DataFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleRepository {
    private static final String HEADER =
            "id,clientId,sellerEmail,createdAt,status,paymentMethod,amountReceived,items";
    private final Path path;

    public SaleRepository() throws IOException {
        path = DataFiles.data("Sales.csv");
        DataFiles.ensure(path, HEADER);
    }

    public synchronized Sale create(Sale sale) throws IOException {
        sale.setId(DataFiles.nextId(findAll().stream().map(Sale::getId).toList()));
        List<String> lines = Files.readAllLines(path);
        lines.add(toCsv(sale));
        DataFiles.writeAtomically(path, lines);
        return sale;
    }

    public synchronized List<Sale> findAll() throws IOException {
        List<Sale> sales = new ArrayList<>();
        List<String> lines = Files.readAllLines(path);
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                sales.add(fromCsv(lines.get(i)));
            }
        }
        return sales;
    }

    public synchronized Sale findById(long id) throws IOException {
        return findAll().stream().filter(sale -> sale.getId() == id).findFirst().orElse(null);
    }

    public synchronized void update(Sale updated) throws IOException {
        List<Sale> sales = findAll();
        int index = -1;
        for (int i = 0; i < sales.size(); i++) {
            if (sales.get(i).getId().equals(updated.getId())) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            throw new IllegalArgumentException("Venda nao encontrada.");
        }
        sales.set(index, updated);
        saveAll(sales);
    }

    private void saveAll(List<Sale> sales) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        sales.forEach(sale -> lines.add(toCsv(sale)));
        DataFiles.writeAtomically(path, lines);
    }

    private String toCsv(Sale sale) {
        String items = sale.getItems().stream()
                .map(item -> item.productId() + ";" + encode(item.productName()) + ";"
                        + item.quantity() + ";" + item.unitPrice())
                .reduce((left, right) -> left + "|" + right)
                .orElse("");
        return CsvUtil.line(sale.getId(), sale.getClientId(), sale.getSellerEmail(),
                sale.getCreatedAt(), sale.getStatus(),
                sale.getPaymentMethod() == null ? "" : sale.getPaymentMethod(),
                sale.getAmountReceived(), items);
    }

    private Sale fromCsv(String csv) {
        List<String> data = CsvUtil.parse(csv);
        List<SaleItem> items = new ArrayList<>();
        for (String item : data.get(7).split("\\|")) {
            String[] fields = item.split(";", -1);
            items.add(new SaleItem(Long.parseLong(fields[0]), decode(fields[1]),
                    Integer.parseInt(fields[2]), Double.parseDouble(fields[3])));
        }
        PaymentMethod payment = data.get(5).isBlank() ? null : PaymentMethod.valueOf(data.get(5));
        return new Sale(Long.parseLong(data.get(0)), Long.parseLong(data.get(1)), data.get(2),
                LocalDateTime.parse(data.get(3)), SaleStatus.valueOf(data.get(4)), payment,
                Double.parseDouble(data.get(6)), items);
    }

    private String encode(String value) {
        return value.replace("%", "%25").replace("|", "%7C").replace(";", "%3B");
    }

    private String decode(String value) {
        return value.replace("%3B", ";").replace("%7C", "|").replace("%25", "%");
    }
}
