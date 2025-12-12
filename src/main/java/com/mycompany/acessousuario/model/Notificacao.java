package com.mycompany.acessousuario.model;

/**
 *
 * @author marqu
 */
public class Notificacao {
    private int id;
    private int idRemetente;
    private int idUsuario;
    private String conteudo;
    private boolean lida;
    private String dataEnvio;
    
    public Notificacao() {
    }
    
    public Notificacao(int idRemetente, int idUsuario, String conteudo, String dataEnvio) {
        this.idRemetente = idRemetente;
        this.idUsuario = idUsuario;
        this.conteudo = conteudo;
        this.lida = false;
        this.dataEnvio = dataEnvio;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdRemetente() {
        return idRemetente;
    }
    
    public void setIdRemetente(int idRemetente) {
        this.idRemetente = idRemetente;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getConteudo() {
        return conteudo;
    }
    
    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
    
    public boolean isLida() {
        return lida;
    }
    
    public void setLida(boolean lida) {
        this.lida = lida;
    }
    
    public String getDataEnvio() {
        return dataEnvio;
    }
    
    public void setDataEnvio(String dataEnvio) {
        this.dataEnvio = dataEnvio;
    }
}

