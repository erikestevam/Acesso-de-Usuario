package com.mycompany.acessousuario.log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CSVLogWriter implements ILogWriter {
    
    private static final String ARQUIVO_LOG = "log.csv";
    private PrintWriter writer;

    public CSVLogWriter() throws IOException {
        Path path = Paths.get(ARQUIVO_LOG);
        boolean arquivoExiste = Files.exists(path);
        
        writer = new PrintWriter(new FileWriter(ARQUIVO_LOG, true));
        
        if (!arquivoExiste || Files.size(path) == 0) {
            escreverCabecalho();
        }
    }

    private void escreverCabecalho() {
        writer.println("OPERACAO;NOME;DATA;HORA;USUARIO");
        writer.flush();
    }

    @Override
    public void escreverLog(String operacao, String nomeUsuario, String data, String hora, String usuarioAutenticado) {
        writer.println(String.format("%s;%s;%s;%s;%s", 
            operacao, nomeUsuario, data, hora, usuarioAutenticado));
        writer.flush();
    }

    @Override
    public void escreverLogFalha(String operacao, String nomeUsuario, String mensagemErro, String data, String hora, String usuarioAutenticado) {
        writer.println(String.format("FALHA: %s;%s;%s;%s;%s;%s", 
            operacao, mensagemErro, nomeUsuario, data, hora, usuarioAutenticado));
        writer.flush();
    }

    @Override
    public void fechar() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}

