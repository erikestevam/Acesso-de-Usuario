package com.mycompany.acessousuario.log;

import java.io.IOException;

public interface ILogWriter {
    void escreverLog(String operacao, String nomeUsuario, String data, String hora, String usuarioAutenticado) throws IOException;
    void escreverLogFalha(String operacao, String nomeUsuario, String mensagemErro, String data, String hora, String usuarioAutenticado) throws IOException;
    void fechar() throws IOException;
}



