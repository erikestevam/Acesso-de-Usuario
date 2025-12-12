package repository;

import java.sql.SQLException;

public interface IConfiguracaoRepository {
    String obterValor(String chave) throws SQLException;
    void salvarValor(String chave, String valor) throws SQLException;
}



