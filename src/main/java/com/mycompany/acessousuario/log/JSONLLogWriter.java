package com.mycompany.acessousuario.log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class JSONLLogWriter implements ILogWriter {
    
    private static final String ARQUIVO_LOG = "log.jsonl";
    private PrintWriter writer;

    public JSONLLogWriter() throws IOException {
        writer = new PrintWriter(new FileWriter(ARQUIVO_LOG, true));
    }

    @Override
    public void escreverLog(String operacao, String nomeUsuario, String data, String hora, String usuarioAutenticado) {
        String json = String.format(
            "{\"operacao\":\"%s\",\"nome\":\"%s\",\"data\":\"%s\",\"hora\":\"%s\",\"usuario\":\"%s\"}",
            escapeJson(operacao), escapeJson(nomeUsuario), escapeJson(data), 
            escapeJson(hora), escapeJson(usuarioAutenticado)
        );
        writer.println(json);
        writer.flush();
    }

    @Override
    public void escreverLogFalha(String operacao, String nomeUsuario, String mensagemErro, String data, String hora, String usuarioAutenticado) {
        String json = String.format(
            "{\"operacao\":\"%s\",\"tipo\":\"falha\",\"mensagem\":\"%s\",\"nome\":\"%s\",\"data\":\"%s\",\"hora\":\"%s\",\"usuario\":\"%s\"}",
            escapeJson(operacao), escapeJson(mensagemErro), escapeJson(nomeUsuario), 
            escapeJson(data), escapeJson(hora), escapeJson(usuarioAutenticado)
        );
        writer.println(json);
        writer.flush();
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    @Override
    public void fechar() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }
}

