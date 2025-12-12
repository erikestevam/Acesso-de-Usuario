/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presenter;

import com.mycompany.acessousuario.model.Usuario;
import javax.swing.JMenuItem;
import repository.IUsuarioRepository;
import repository.INotificacaoRepository;
import view.AlterarSenhaView;
import view.ControleUsuariosView;
import view.MainMDIView;
import com.mycompany.acessousuario.dao.NotificacaoDAO;

/**
 *
 * @author marqu
 */

public class MainMDIViewPresenter {
    
    private final MainMDIView view;
    private final Usuario usuarioAutenticado;
    private final IUsuarioRepository usuarioRepository;
    private final INotificacaoRepository notificacaoRepository;

    public MainMDIViewPresenter(MainMDIView view, Usuario usuarioAutenticado, IUsuarioRepository usuarioRepository) {
        this.view = view;
        this.usuarioAutenticado = usuarioAutenticado;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoRepository = new NotificacaoDAO();
        
        configurarInterface();
        configurarAcoesMenu();
    }
    
    private void configurarAcoesMenu() {
        // Configurar ação do menu "Controle de Usuarios" para abrir ControleUsuariosView
        view.getControleUsuariosMenuItem().addActionListener(e -> abrirControleUsuarios());
    }
    
    private void abrirControleUsuarios() {
        // Verificar se já existe uma ControleUsuariosView aberta
        javax.swing.JInternalFrame[] frames = view.getDesktopPane().getAllFrames();
        for (javax.swing.JInternalFrame frame : frames) {
            if (frame instanceof ControleUsuariosView) {
                frame.toFront();
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                    // Ignorar se não puder selecionar
                }
                return;
            }
        }
        
        ControleUsuariosView controleView = new ControleUsuariosView(
            usuarioRepository,
            notificacaoRepository,
            usuarioAutenticado
        );
        view.getDesktopPane().add(controleView);
        controleView.setVisible(true);
        controleView.setLocation(50, 50);
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
            // Mostrar todos os menus de administrador
            cadastrarUsuarioItem.setVisible(true);
            view.getControleUsuariosMenuItem().setVisible(true);
            view.getConfgMenu().setVisible(true); // Restaurar visibilidade do menu de configuração
            view.getListarUsuarios().setVisible(true); // Restaurar visibilidade de listar usuários
            view.getEnviarNotificacao().setVisible(true); // Restaurar visibilidade de enviar notificação
        } else {
            // Esconder menus de administrador para usuários comuns
            cadastrarUsuarioItem.setVisible(false);
            view.getConfgMenu().setVisible(false);
            view.getListarUsuarios().setVisible(false);
            view.getEnviarNotificacao().setVisible(false);
            view.getControleUsuariosMenuItem().setVisible(false);
        }
        
        // Mostrar item de deslogar e esconder "Fazer Login" quando há usuário autenticado
        view.getDeslogarMenuItem().setVisible(true);
        view.getFazerLoginMenuItem().setVisible(false);
    }
    
    private void abrirAlterarSenha() {
        MainMDIView mdi = MainMDIView.getInstance(); 

        AlterarSenhaView alterarSenhaView = new AlterarSenhaView();

        new AlterarSenhaPresenter(alterarSenhaView, usuarioRepository, usuarioAutenticado);

        mdi.getDesktopPane().add(alterarSenhaView);
        alterarSenhaView.setVisible(true);
    }
}