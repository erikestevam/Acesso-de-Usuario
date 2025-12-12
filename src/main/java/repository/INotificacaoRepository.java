/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package repository;

import com.mycompany.acessousuario.model.Notificacao;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author marqu
 */
public interface INotificacaoRepository {
    int contarNotificacoesLidas(int idUsuario) throws SQLException;
    int contarEnviadasPeloUsuario(int idRemetente) throws SQLException; 
    int contarNaoLidas(int idUsuario) throws SQLException;
    void salvar(Notificacao notificacao) throws SQLException;
    List<Notificacao> listarPorUsuario(int idUsuario) throws SQLException;
    void marcarComoLida(int idNotificacao) throws SQLException;
}
