package com.pi.model;

import java.time.LocalDateTime;
import java.util.List;

public class Sale {
    private Long id;
    private final long clientId;
    private final String sellerEmail;
    private final LocalDateTime createdAt;
    private SaleStatus status;
    private PaymentMethod paymentMethod;
    private double amountReceived;
    private final List<SaleItem> items;

    public Sale(Long id, long clientId, String sellerEmail, LocalDateTime createdAt,
                SaleStatus status, PaymentMethod paymentMethod, double amountReceived,
                List<SaleItem> items) {
        this.id = id;
        this.clientId = clientId;
        this.sellerEmail = sellerEmail;
        this.createdAt = createdAt;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.amountReceived = amountReceived;
        this.items = List.copyOf(items);
        if (items.isEmpty()) {
            throw new IllegalArgumentException("A venda deve possuir ao menos um item.");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getClientId() {
        return clientId;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public SaleStatus getStatus() {
        return status;
    }

    public void setStatus(SaleStatus status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(double amountReceived) {
        this.amountReceived = amountReceived;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public double getTotal() {
        return items.stream().mapToDouble(SaleItem::subtotal).sum();
    }
}
