package com.pi.model;

import java.time.LocalDateTime;

public class Prestador extends Usuario {

    private Categoria categoria;
    private String descricaoProfissional;
    private String endereco;
    private String cidade;
    private Estado estado;
    private StatusAprovacao statusAprovacao;

    public Prestador(long id, String nome, String email, String cpf, String senha,
                     TipoUsuario tipoUsuario, LocalDateTime dataCadastro, Status status,
                     Categoria categoria, String descricaoProfissional, String endereco,
                     String cidade, Estado estado, StatusAprovacao statusAprovacao) {
        super(id, nome, email, cpf, senha, tipoUsuario, dataCadastro, status);
        this.categoria = categoria;
        this.descricaoProfissional = descricaoProfissional;
        this.endereco = endereco;
        this.cidade = cidade;
        this.estado = estado;
        this.statusAprovacao = statusAprovacao;
    }

    public Prestador(String nome, String email, String cpf, String senha,
                     TipoUsuario tipoUsuario, LocalDateTime dataCadastro, Status status,
                     Categoria categoria, String descricaoProfissional, String endereco,
                     String cidade, Estado estado, StatusAprovacao statusAprovacao) {
        this(-1, nome, email, cpf, senha, tipoUsuario, dataCadastro, status, categoria,
                descricaoProfissional, endereco, cidade, estado, statusAprovacao);
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDescricaoProfissional() {
        return descricaoProfissional;
    }

    public void setDescricaoProfissional(String descricaoProfissional) {
        this.descricaoProfissional = descricaoProfissional;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public StatusAprovacao getStatusAprovacao() {
        return statusAprovacao;
    }

    public void setStatusAprovacao(StatusAprovacao statusAprovacao) {
        this.statusAprovacao = statusAprovacao;
    }
}
