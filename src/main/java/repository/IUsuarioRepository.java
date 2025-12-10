/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package repository;

import com.mycompany.acessousuario.model.Usuario;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author marqu
 */
public interface IUsuarioRepository {
    
    void salvar(Usuario u) throws SQLException;
    List<Usuario> listarTodos() throws SQLException;
    void atualizar(Usuario u) throws SQLException;
    void deletar(int id) throws SQLException;
    Usuario autenticar(String login, String senha) throws SQLException;
    boolean verificarLoginExistente(String login) throws SQLException;
    int contarUsuarios() throws SQLException;
    public void atualizarSenha(int idUsuario, String novaSenha) throws SQLException;
    Usuario buscarPorLogin(String login) throws SQLException;
    public void atualizarUsuario(Usuario u) throws SQLException;
}
