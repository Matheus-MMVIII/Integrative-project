package com.pi.model;

public enum PaymentMethod {
    CASH("Dinheiro"),
    PIX("Pix"),
    CREDIT_CARD("Cartao de credito"),
    DEBIT_CARD("Cartao de debito");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
