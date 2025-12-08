package com.mycompany.acessousuario.model;

import java.time.LocalDateTime;

public class Usuario {
    private int id;
    private String nome;
    private String login;
    private String senha;
    private String email;
    private String tipo;
    private String data_criacao;
    private boolean ativo;

    public Usuario() {
    }

    public Usuario(int id, String nome, String login, String senha, String email, String tipo, String data_criacao, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.email = email;
        this.tipo = tipo;
        this.data_criacao = data_criacao;
        this.ativo = ativo;
    }

    public Usuario(String nome, String login, String senha, String email, String tipo) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.email = email;
        this.tipo = tipo;
        this.ativo = false; 
        this.data_criacao = LocalDateTime.now().toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getData_criacao() {
        return data_criacao;
    }

    public void setData_criacao(String data_criacao) {
        this.data_criacao = data_criacao;
    }

    public boolean isAtivo() { 
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", nome=" + nome + ", login=" + login + ", email=" + email + ", tipo=" + tipo + ", data_criacao=" + data_criacao + ", ativo=" + ativo + '}';
    }
}
