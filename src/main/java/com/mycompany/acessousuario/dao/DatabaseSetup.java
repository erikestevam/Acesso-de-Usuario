package com.mycompany.acessousuario.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseSetup {

    private static final String DB_URL = "jdbc:sqlite:usuarios.db";

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nome TEXT NOT NULL,"
                + "login TEXT NOT NULL UNIQUE,"
                + "senha TEXT NOT NULL,"
                + "email TEXT,"
                + "tipo TEXT NOT NULL"
                + ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
            System.out.println("Tabela 'usuarios' criada ou já existente.");
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela: " + e.getMessage());
        }
    }
}
