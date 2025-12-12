package com.mycompany.acessousuario.log;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogManager {
    
    private static LogManager instance;
    private ILogWriter logWriter;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private LogManager() {
        // Por padrão, usa CSV. Pode ser alterado via configuração
        try {
            this.logWriter = new CSVLogWriter();
        } catch (IOException e) {
            System.err.println("Erro ao inicializar log: " + e.getMessage());
        }
    }
    
    public static LogManager getInstance() {
        if (instance == null) {
            instance = new LogManager();
        }
        return instance;
    }
    
    public void definirFormato(String formato) throws IOException {
        if (logWriter != null) {
            logWriter.fechar();
        }
        
        if ("JSONL".equalsIgnoreCase(formato)) {
            logWriter = new JSONLLogWriter();
        } else {
            logWriter = new CSVLogWriter();
        }
    }
    
    public void logarOperacao(String operacao, String nomeUsuario, String usuarioAutenticado) {
        if (logWriter == null) return;
        
        try {
            LocalDateTime agora = LocalDateTime.now();
            String data = agora.format(DATE_FORMATTER);
            String hora = agora.format(TIME_FORMATTER);
            
            logWriter.escreverLog(operacao, nomeUsuario, data, hora, usuarioAutenticado);
        } catch (IOException e) {
            System.err.println("Erro ao escrever log: " + e.getMessage());
        }
    }
    
    public void logarFalha(String operacao, String nomeUsuario, String mensagemErro, String usuarioAutenticado) {
        if (logWriter == null) return;
        
        try {
            LocalDateTime agora = LocalDateTime.now();
            String data = agora.format(DATE_FORMATTER);
            String hora = agora.format(TIME_FORMATTER);
            
            logWriter.escreverLogFalha(operacao, nomeUsuario, mensagemErro, data, hora, usuarioAutenticado);
        } catch (IOException e) {
            System.err.println("Erro ao escrever log de falha: " + e.getMessage());
        }
    }
    
    public void fechar() throws IOException {
        if (logWriter != null) {
            logWriter.fechar();
        }
    }
}



