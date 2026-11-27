package com.pi.persistence;

import com.pi.model.Usuario;

import java.sql.SQLException;

public interface ICRUD {

    public void inserir(Usuario usuario) throws SQLException;

    public Usuario consultar(int id) throws SQLException;

    public void alterar(Usuario usuario) throws SQLException;

    public void excluir(int id) throws SQLException;
}
