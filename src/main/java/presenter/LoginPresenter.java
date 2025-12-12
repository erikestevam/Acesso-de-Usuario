/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presenter;

import com.mycompany.acessousuario.model.Usuario;
import java.sql.SQLException;
import repository.IUsuarioRepository;
import view.LoginView;
import view.MainMDIView;

/**
 *
 * @author marqu
 */
public class LoginPresenter {
    
    private final LoginView view;
    private final IUsuarioRepository repository;
    
    public LoginPresenter(LoginView view, IUsuarioRepository repository){
        this.view = view;
        this.repository = repository;
    }
    
    public void verificarEstadoInicial() {
        // Método mantido para compatibilidade, mas não faz nada
        // O LoginView já é aberto diretamente no MainMDIView
    }
    
    public void processarLogin(){
        String login = view.getLogin();
        String senha = view.getSenha();
        
        if(login.isEmpty() || senha.isEmpty()){
            view.exibirMensagem("Usuario e senha são obrigatorios.");
            return;
        }
        
        Usuario usuarioAutenticado = null;
        try{
            //autenticar no banco
            usuarioAutenticado = repository.autenticar(login,senha);
            
            if(usuarioAutenticado == null){
                view.exibirMensagem("Usurio ou senha inválidos.");
                return;
            }
            
            //verificar ativo
            if(!usuarioAutenticado.isAtivo()){
                view.exibirMensagem("Seu acesso ainda não foi autorizado pelo administrador.");
                return;
            }
            
            //login deu certo
            view.exibirMensagem("Login bem-sucedido!\n Bem Vindo(a), " + usuarioAutenticado.getNome());
            
            abrirTelaPrincipal(usuarioAutenticado);
            view.dispose();
        } catch(SQLException e){
            view.exibirMensagem("Erro ao acessar o banco de dados" + e.getMessage());
        }
    }
    
    private void abrirTelaPrincipal(Usuario usuarioAutenticado) {
        MainMDIView mainView = MainMDIView.getInstance();
        new MainMDIViewPresenter(mainView, usuarioAutenticado, repository); 
        mainView.setVisible(true);
        
        // Mostrar item de deslogar e esconder "Fazer Login" após login bem-sucedido
        mainView.getDeslogarMenuItem().setVisible(true);
        mainView.getFazerLoginMenuItem().setVisible(false);
    }
}
