/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presenter;

import com.mycompany.acessousuario.model.Usuario;
import java.sql.SQLException;
import repository.IUsuarioRepository;
import view.CadastroUsuarioView;
import view.LoginView;
import view.MainMDIView;

/**
 *
 * @author marqu
 */
public class LoginPresenter {
    
    private final LoginView view;
    private final IUsuarioRepository repository;
    private final MainMDIView mdiParent;
    
    public LoginPresenter(LoginView view, IUsuarioRepository repository, MainMDIView mdiParent){
        this.view = view;
        this.repository = repository;
        this.mdiParent = mdiParent;
    }
    
    public void verificarEstadoInicial() {
        try {
            int totalUsuarios = repository.contarUsuarios();
            
            if (totalUsuarios == 0) {
                view.exibirMensagem("Primeiro acesso ao sistema! Você deve realizar o cadastro inicial de Administrador.");
                abrirCadastroInicial(); 
            } 
            
        } catch (SQLException e) {
            view.exibirMensagem("Erro crítico ao verificar o estado inicial do sistema.");
        }
    }
    
    private void abrirCadastroInicial() {
        CadastroUsuarioView cadastroView = new CadastroUsuarioView();
        mdiParent.getDesktopPane().add(cadastroView); // Assumindo que sua MDI tem um método getDesktopPane()
        cadastroView.setVisible(true);
        view.fechar();
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
        MainMDIView mainView = new MainMDIView();
        mainView.setVisible(true);
    }
}
