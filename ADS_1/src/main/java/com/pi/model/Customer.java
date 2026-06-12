package com.pi.model;

public class Customer {
    private final String NAME;
    private final String IDENTIFIER;
    private Product[] shoppingCart;

    public Customer(String name, String identifier) {
        this.NAME = name;
        this.IDENTIFIER = idValidator(identifier);
        this.shoppingCart = new Product[99];
    }

    private String idValidator(String id) {
        id = id.trim()
                .replace(".", "")
                .replace("-", "")
                .replace("/", "");
        if (id.length() == 11) {
            Cpf cpf = new Cpf(id);
            return cpf.getCpf();
        }else if (id.length() == 14) {
            return "";
        }else {
            throw new IllegalArgumentException("Invalid CPF/CNPJ. ");
        }
    }
}
