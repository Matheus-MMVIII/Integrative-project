package com.pi.utils;

import com.pi.model.Client;
import com.pi.model.Product;

public final class CsvUtil {

    private CsvUtil() {
    }

    public static String product(Product product) {
        return product.getId()+","+product.getName()+","+product.getPrice()+","+product.getStock();
    }

    public static Product toProduct(String csv) {
        String[] data = csv.split(",");
        return new Product(data[0], Double.parseDouble(data[1]), Integer.parseInt(data[2]), Long.parseLong(data[3]));
    }

    public static String client(Client client) {
        return client.getId()+","+ client.getName()+","+ client.getPrice()+","+ client.getStock();
    }

    public static Client toClient(String csv) {
        String[] data = csv.split(",");
        return new Client(data[0], Double.parseDouble(data[1]), Integer.parseInt(data[2]), Long.parseLong(data[3]));
    }

}
