/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presenter;

import com.mycompany.acessousuario.dao.UsuarioDAO;
import com.mycompany.acessousuario.model.Usuario;
import javax.swing.JMenuItem;
import repository.IUsuarioRepository;
import view.AlterarSenhaView;
import view.CadastroUsuarioView;
import view.ConfiguracaoView;
import view.EnviarNotificacaoView;
import view.ListarNotificacoesView;
import view.ListarUsuariosView;
import view.MainMDIView;

/**
 *
 * @author marqu
 */

public class MainMDIViewPresenter {
    
    private final MainMDIView view;
    private final Usuario usuarioAutenticado;

    public MainMDIViewPresenter(MainMDIView view, Usuario usuarioAutenticado) {
        this.view = view;
        this.usuarioAutenticado = usuarioAutenticado;
        
        configurarInterface();
        adicionarListeners();
    }

    private void adicionarListeners() {
        view.getListarUsuarios().addActionListener(e -> abrirListarUsuarios());
        view.getEnviarNotificacao().addActionListener(e -> abrirEnviarNotificacao());
        view.getListarNotificacao().addActionListener(e -> abrirListarNotificacoes());
        view.getAlterarSenha().addActionListener(e -> abrirAlterarSenha());
        view.getCadastrarUsuario().addActionListener(e -> abrirCadastroUsuario());
        view.getConfg().addActionListener(e -> abirConfiguracao());
        
    }
    
    private void abrirListarUsuarios() {
        MainMDIView mdi = MainMDIView.getInstance(); // Pega a instância única da tela principal

        ListarUsuariosView listarView = new ListarUsuariosView(usuarioAutenticado);

        mdi.getDesktopPane().add(listarView);
        listarView.setVisible(true);
    }

    private void abrirAlterarSenha() {
        MainMDIView mdi = MainMDIView.getInstance(); 

        AlterarSenhaView alterarSenhaView = new AlterarSenhaView();

        IUsuarioRepository uRepo = new UsuarioDAO(); // Injetar a dependência concreta
        new AlterarSenhaPresenter(alterarSenhaView, uRepo, usuarioAutenticado);

        mdi.getDesktopPane().add(alterarSenhaView);
        alterarSenhaView.setVisible(true);
    }

    private void abrirCadastroUsuario() {
        MainMDIView mdi = MainMDIView.getInstance();
        CadastroUsuarioView view = new CadastroUsuarioView(); 
        mdi.getDesktopPane().add(view);
        view.setVisible(true);
    }

    private void abrirListarNotificacoes() {
        MainMDIView mdi = MainMDIView.getInstance();
        ListarNotificacoesView view = new ListarNotificacoesView(); 
        mdi.getDesktopPane().add(view);
        view.setVisible(true);
    }

    private void abrirEnviarNotificacao() {
        MainMDIView mdi = MainMDIView.getInstance();
        EnviarNotificacaoView view = new EnviarNotificacaoView();
        mdi.getDesktopPane().add(view);
        view.setVisible(true);
    }

    private void abirConfiguracao(){
        MainMDIView mdi = MainMDIView.getInstance();
        ConfiguracaoView view = new ConfiguracaoView();
        mdi.getDesktopPane().add(view);
        view.setVisible(true);
    }
    
    private void configurarInterface() {
        // Configura Rodapé
        String info = String.format("Usuário: %s | Perfil: %s", 
                                    usuarioAutenticado.getNome(), 
                                    usuarioAutenticado.getTipo());
        view.setRodapeInfo(info);
        
        // Configura Menus e Acessos
        JMenuItem cadastrarUsuarioItem = view.getSaveAsMenuItem();
        
        if (usuarioAutenticado.getTipo().equals("Administrador") || usuarioAutenticado.getTipo().equals("AdiministradorChefe")) {
            cadastrarUsuarioItem.setVisible(true);
        } else {
            cadastrarUsuarioItem.setVisible(false);
            view.getConfgMenu().setVisible(false);
            view.getListarUsuarios().setVisible(false);
            view.getListarNotificacao().setVisible(false);
        }
    }
}