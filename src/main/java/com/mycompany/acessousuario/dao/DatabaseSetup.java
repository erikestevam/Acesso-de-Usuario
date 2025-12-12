package com.mycompany.acessousuario.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    private static final String DB_URL = "jdbc:sqlite:usuarios.db";

    public static void inicializarBanco() {
        criarTabelaUsuarios();
        migrarTabelaUsuarios(); // Adiciona colunas se não existirem
        criarTabelaNotificacoes();
        criarTabelaConfiguracao();
    }
    
    private static void migrarTabelaUsuarios() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Verificar se a coluna data_criacao existe
            boolean temDataCriacao = false;
            boolean temAtivo = false;
            
            try (ResultSet rs = stmt.executeQuery("PRAGMA table_info(usuarios)")) {
                while (rs.next()) {
                    String nomeColuna = rs.getString("name");
                    if ("data_criacao".equals(nomeColuna)) {
                        temDataCriacao = true;
                    }
                    if ("ativo".equals(nomeColuna)) {
                        temAtivo = true;
                    }
                }
            }
            
            // Adicionar coluna data_criacao se não existir
            if (!temDataCriacao) {
                stmt.execute("ALTER TABLE usuarios ADD COLUMN data_criacao TEXT");
                // Atualizar registros existentes com data atual
                stmt.execute("UPDATE usuarios SET data_criacao = datetime('now') WHERE data_criacao IS NULL OR data_criacao = ''");
                System.out.println("Coluna 'data_criacao' adicionada à tabela usuarios.");
            }
            
            // Adicionar coluna ativo se não existir
            if (!temAtivo) {
                // SQLite não suporta NOT NULL em ALTER TABLE ADD COLUMN diretamente
                // Vamos adicionar sem NOT NULL e depois atualizar valores nulos
                stmt.execute("ALTER TABLE usuarios ADD COLUMN ativo INTEGER DEFAULT 1");
                // Atualizar registros existentes para ativo (assumindo que já existentes estão ativos)
                stmt.execute("UPDATE usuarios SET ativo = 1 WHERE ativo IS NULL");
                System.out.println("Coluna 'ativo' adicionada à tabela usuarios.");
            }
            
        } catch (SQLException e) {
            System.out.println("Erro ao migrar tabela usuarios: " + e.getMessage());
        }
    }

    private static void criarTabelaUsuarios() {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nome TEXT NOT NULL,"
                + "login TEXT NOT NULL UNIQUE,"
                + "senha TEXT NOT NULL,"
                + "email TEXT,"
                + "tipo TEXT NOT NULL,"
                + "data_criacao TEXT NOT NULL,"
                + "ativo INTEGER NOT NULL DEFAULT 0"
                + ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela 'usuarios' criada ou já existente.");
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela usuarios: " + e.getMessage());
        }
    }

    private static void criarTabelaNotificacoes() {
        String sql = "CREATE TABLE IF NOT EXISTS notificacoes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "id_remetente INTEGER NOT NULL,"
                + "id_usuario INTEGER NOT NULL,"
                + "conteudo TEXT NOT NULL,"
                + "lida INTEGER NOT NULL DEFAULT 0,"
                + "data_envio TEXT NOT NULL,"
                + "FOREIGN KEY (id_remetente) REFERENCES usuarios(id),"
                + "FOREIGN KEY (id_usuario) REFERENCES usuarios(id)"
                + ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela 'notificacoes' criada ou já existente.");
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela notificacoes: " + e.getMessage());
        }
    }

    private static void criarTabelaConfiguracao() {
        String sql = "CREATE TABLE IF NOT EXISTS configuracao ("
                + "chave TEXT PRIMARY KEY,"
                + "valor TEXT NOT NULL"
                + ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela 'configuracao' criada ou já existente.");
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela configuracao: " + e.getMessage());
        }
    }
}
