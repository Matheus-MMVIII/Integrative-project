package com.pi.model;

public class Client {

    private Long id;
    private String name;
    private Cpf cpf;
    private String phone;
    private String email;
    private String origin;

    public Client(Long id, String name, String cpf, String phone, String email, String origin) {
        this.id = id;
        this.name = name;
        this.cpf = new Cpf(cpf);
        this.phone = phone;
        this.email = email;
        this.origin = origin;
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
        this.name = name;
    }

    public String getCpf() {
        return cpf.getCpf();
    }

    public void setCpf(String cpf) {
        this.cpf = new Cpf(cpf);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}