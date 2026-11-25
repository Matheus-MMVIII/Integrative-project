package com.pi.model;

import java.time.LocalDateTime;

public class Usuario {

    private long id;
    private String nome;
    private String email;
    private String cpf;
    private String senha;
    private TipoUsuario tipoUsuario;
    private LocalDateTime dataCadastro;
    private Status status;

    public Usuario(long id, String nome, String email, String cpf, String senha,
                   TipoUsuario tipoUsuario, LocalDateTime dataCadastro, Status status) {
        setIdUsuario(id);
        setNome(nome);
        setEmail(email);
        setCpf(cpf);
        setSenha(senha);
        setTipoUsuario(tipoUsuario);
        setDataCadastro(dataCadastro);
        setStatus(status);
    }

    public Usuario(String nome, String email, String cpf, String senha,
                   TipoUsuario tipoUsuario, LocalDateTime dataCadastro, Status status) {
        this(-1, nome, email, cpf, senha, tipoUsuario, dataCadastro, status);
    }

    public long getIdUsuario() {
        return id;
    }

    public void setIdUsuario(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome.matches("^[\\\\p{L}´^~]+$")) {
            this.nome = nome;
            return;
        }
        throw new IllegalArgumentException("Nome inválido. ");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email.matches(".*@.*")) {
            this.email = email;
            return;
        }
        throw new IllegalArgumentException("Email inválido. ");
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (CpfValidator.isValid(cpf)) {
            this.cpf = cpf;
            return;
        }
        throw new IllegalArgumentException("Cpf inválido. ");
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        if (senha.matches("^(?=.{8,30}$)(?=.*[^\\\\p{L}\\\\p{N}\\\\s]).+$")) {
            this.senha = senha;
            return;
        }
        throw new IllegalArgumentException("Senha inválido. ");
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
