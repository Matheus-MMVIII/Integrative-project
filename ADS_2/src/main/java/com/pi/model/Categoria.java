package com.pi.model;

public class Categoria {

    private String nome;
    private String descricao;
    private StatusCategoria statusCategoria;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusCategoria getStatusCategoria() {
        return statusCategoria;
    }

    public void setStatusCategoria(StatusCategoria statusCategoria) {
        this.statusCategoria = statusCategoria;
    }
}
