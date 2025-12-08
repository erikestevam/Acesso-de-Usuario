/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presenter;

import com.mycompany.acessousuario.model.Usuario;
import java.sql.SQLException;
import java.util.List;
import repository.IUsuarioRepository;
import view.CadastroUsuarioView;

/**
 *
 * @author marqu
 */
public class CadastroUsuarioPresenter {
    
    public final CadastroUsuarioView view;
    public final IUsuarioRepository repository;
    
    public CadastroUsuarioPresenter(CadastroUsuarioView view,IUsuarioRepository repository){
        this.view = view;
        this.repository = repository;
    }
    
    public void processarCadastro(){
        String login = view.getLogin();
        String senha = view.getSenha();
        String confirmaSenha = view.getConfirmaSenha();
        
        //validar dados
        if(login.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()){
            view.exibirMensagem("Todos os campos devem ser preenchidos.");
            return;
        }
        
        if(!senha.equals(confirmaSenha)){
            view.exibirMensagem("A senha e a confirmação de senha não coincidem.");
            return;
        }
        
        try{
            //verificar se o login ja existe
            if(repository.verificarLoginExistente(login)){
                view.exibirMensagem("Este nome de usuário já está em uso.");
                return;
            }
            
            //verificar se é o primeiro usuario
            List<Usuario> usuarioExistentes = repository.listarTodos();
            
            String tipoUsuario;
            boolean ativo;
            
            if(usuarioExistentes.isEmpty()){
                tipoUsuario = "AdiministradorChefe";
                ativo = true;
                view.exibirMensagem("Cadastro realizado com sucesso! Você é o Administrador inicial.");
            }else{
                tipoUsuario = "Usuário";
                ativo = false;
                view.exibirMensagem("Solicitação de cadastro enviada. Seu acesso será liberado após a autorização do Administrador.");
            }
            
            Usuario novoUsuario = new Usuario(login, login, senha, "", tipoUsuario);
            novoUsuario.setAtivo(ativo);
            
            repository.salvar(novoUsuario);
            
            view.limparCampos();
            
        }catch (SQLException e) {
            view.exibirMensagem("Erro ao salvar no banco de dados: " + e.getMessage());
        }
    }
}
