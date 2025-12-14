package com.mycompany.acessousuario.dao;

import com.mycompany.acessousuario.model.Notificacao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import repository.INotificacaoRepository;

/**
 *
 * @author marqu
 */
public class NotificacaoDAO implements INotificacaoRepository{
    
    private static final String DB_URL = "jdbc:sqlite:usuarios.db";
    
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
    
    @Override
    public void salvar(Notificacao notificacao) throws SQLException {
        String sql = "INSERT INTO notificacoes (id_remetente, id_usuario, conteudo, lida, data_envio) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, notificacao.getIdRemetente());
            stmt.setInt(2, notificacao.getIdUsuario());
            stmt.setString(3, notificacao.getConteudo());
            stmt.setInt(4, notificacao.isLida() ? 1 : 0);
            stmt.setString(5, notificacao.getDataEnvio());
            
            stmt.executeUpdate();
        }
    }
    
    @Override
    public List<Notificacao> listarPorUsuario(int idUsuario) throws SQLException {
        List<Notificacao> notificacoes = new ArrayList<>();
        String sql = "SELECT * FROM notificacoes WHERE id_usuario = ? ORDER BY data_envio DESC";
        
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Notificacao n = new Notificacao();
                    n.setId(rs.getInt("id"));
                    n.setIdRemetente(rs.getInt("id_remetente"));
                    n.setIdUsuario(rs.getInt("id_usuario"));
                    n.setConteudo(rs.getString("conteudo"));
                    n.setLida(rs.getInt("lida") == 1);
                    n.setDataEnvio(rs.getString("data_envio"));
                    
                    notificacoes.add(n);
                }
            }
        }
        return notificacoes;
    }
    
    @Override
    public void marcarComoLida(int idNotificacao) throws SQLException {
        String sql = "UPDATE notificacoes SET lida = 1 WHERE id = ?";
        
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idNotificacao);
            stmt.executeUpdate();
        }
    }
}
