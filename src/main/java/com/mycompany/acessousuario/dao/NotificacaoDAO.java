/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.acessousuario.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import repository.INotificacaoRepository;

/**
 *
 * @author marqu
 */
public class NotificacaoDAO implements INotificacaoRepository{
    
    private static final String DB_URL = "jdbc:sqlite:usuarios_teste.db";
    
    private Connection conectar() throws SQLException {
        // Método de conexão direta (sem precisar de uma classe Conexao separada)
        return DriverManager.getConnection(DB_URL);
    }
    
    @Override
    public int contarNotificacoesLidas(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notificacoes WHERE id_usuario = ? AND lida = 1";
        
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // Retorna o valor da primeira coluna (COUNT)
                }
            }
        }
        return 0;
    }
    
    @Override
    public int contarEnviadasPeloUsuario(int idRemetente) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notificacoes WHERE id_remetente = ?";
        
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idRemetente);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); 
                }
            }
        }
        return 0;
    }
    

    @Override
    public int contarNaoLidas(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notificacoes WHERE id_usuario = ? AND lida = 0";
        
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}
