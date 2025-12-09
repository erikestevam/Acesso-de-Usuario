package com.mycompany.acessousuario.dao;

import com.mycompany.acessousuario.model.Usuario;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import repository.IUsuarioRepository;

public class UsuarioDAO implements IUsuarioRepository{

    private static final String DB_URL = "jdbc:sqlite:usuarios_teste.db";

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    @Override
    public void salvar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, login, senha, email, tipo, data_criacao, ativo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getLogin());
            stmt.setString(3, u.getSenha());
            stmt.setString(4, u.getEmail());
            stmt.setString(5, u.getTipo());
            stmt.setString(6, u.getData_criacao());
            stmt.setInt(7, u.isAtivo() ? 1 : 0); //boolean no Java, INTEGER no DB: converte true/false para 1/0
            
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNome(rs.getString("nome"));
                u.setLogin(rs.getString("login"));
                u.setSenha(rs.getString("senha"));
                u.setEmail(rs.getString("email"));
                u.setTipo(rs.getString("tipo"));
                u.setData_criacao(rs.getString("data_criacao"));
                u.setAtivo(rs.getBoolean("ativo")); // O JDBC converte 1/0 para true/false
                
                usuarios.add(u);
            }
        }
        return usuarios;
    }

    @Override
    public void atualizar(Usuario u) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ?, login = ?, senha = ?, email = ?, tipo = ?, data_criacao = ?, ativo = ? WHERE id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getLogin());
            stmt.setString(3, u.getSenha());
            stmt.setString(4, u.getEmail());
            stmt.setString(5, u.getTipo());
            stmt.setString(6, u.getData_criacao());
            stmt.setInt(7, u.isAtivo() ? 1 : 0);
            stmt.setInt(8, u.getId());
            
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public Usuario autenticar(String login, String senha) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, senha);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNome(rs.getString("nome"));
                    u.setLogin(rs.getString("login"));
                    u.setSenha(rs.getString("senha"));
                    u.setEmail(rs.getString("email"));
                    u.setTipo(rs.getString("tipo"));
                    u.setData_criacao(rs.getString("data_criacao"));
                    u.setAtivo(rs.getBoolean("ativo"));
                    
                    return u;
                }
            }
        }
        return null;
    }

    @Override
    public boolean verificarLoginExistente(String login) throws SQLException {
        String sql = "SELECT id FROM usuarios WHERE login = ?";
        try (Connection conn = conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    @Override
    public int contarUsuarios() throws SQLException {
        String sql = "SELECT COUNT(id) AS total FROM usuarios";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }
    
    @Override
    public void atualizarSenha(int idUsuario, String novaSenha) throws SQLException {
    
        String sql = "UPDATE usuarios SET senha = ? WHERE id = ?";

        try (Connection conn = conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novaSenha);
            stmt.setInt(2, idUsuario);

            stmt.executeUpdate();
        }
    }
}
