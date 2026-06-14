package com.pi.utils;

import com.pi.model.Product;

public final class CsvUtil {

    private CsvUtil() {
    }

    public static String product(Product product) {
        return product.getId()+","+product.getName()+","+product.getPrice()+","+product.getStock();
    }

    public static Product buildProduct(String csv) {
        String[] data = csv.split(",");
        return new Product(data[0], Double.parseDouble(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
    }

}
