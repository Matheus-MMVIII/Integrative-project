package com.pi.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexaoBD {

    private static final String URL      = "jdbc:postgresql://localhost:5432/";
    private static final String USUARIO  = "postgres";
    private static final String SENHA    = "123456";

    private ConexaoBD() {
    }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
