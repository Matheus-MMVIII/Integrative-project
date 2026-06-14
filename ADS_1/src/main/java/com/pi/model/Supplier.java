package com.pi.model;

public final class Supplier {
    private Long id;
    private String name;
    private String cnpj;
    private String phone;
    private String email;

    public Supplier(Long id, String name, String cnpj, String phone, String email) {
        this.id = id;
        setName(name);
        setCnpj(cnpj);
        this.phone = phone == null ? "" : phone.trim();
        setEmail(email);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O nome do fornecedor e obrigatorio.");
        }
        this.name = name.trim();
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        String normalized = cnpj == null ? "" : cnpj.replaceAll("\\D", "");
        if (!normalized.matches("\\d{14}")) {
            throw new IllegalArgumentException("CNPJ deve conter 14 digitos.");
        }
        this.cnpj = normalized;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? "" : phone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("E-mail do fornecedor invalido.");
        }
        this.email = email.trim().toLowerCase();
    }
}
