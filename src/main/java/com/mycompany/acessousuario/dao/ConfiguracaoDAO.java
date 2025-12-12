package com.mycompany.acessousuario.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import repository.IConfiguracaoRepository;

public class ConfiguracaoDAO implements IConfiguracaoRepository {
    
    private static final String DB_URL = "jdbc:sqlite:usuarios.db";
    
    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    @Override
    public String obterValor(String chave) throws SQLException {
        String sql = "SELECT valor FROM configuracao WHERE chave = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, chave);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("valor");
                }
            }
        }
        return null;
    }
    
    @Override
    public void salvarValor(String chave, String valor) throws SQLException {
        String sql = "INSERT OR REPLACE INTO configuracao (chave, valor) VALUES (?, ?)";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, chave);
            stmt.setString(2, valor);
            stmt.executeUpdate();
        }
    }
}



