package com.pi.model;

public record SaleItem(long productId, String productName, int quantity, double unitPrice) {
    public SaleItem {
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser positiva.");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("O preco nao pode ser negativo.");
        }
    }

    public double subtotal() {
        return quantity * unitPrice;
    }
}
