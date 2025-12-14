package presenter;

import com.mycompany.acessousuario.model.Usuario;
import javax.swing.JMenuItem;
import repository.IUsuarioRepository;
import repository.INotificacaoRepository;
import view.AlterarSenhaView;
import view.ControleUsuariosView;
import view.EnviarNotificacaoView;
import view.ListarNotificacoesView;
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
        view.getControleUsuariosMenuItem().addActionListener(e -> abrirControleUsuarios());
        view.getEnviarNotificacao().addActionListener(e -> abrirEnviarNotificacao());
        view.getListarNotificacao().addActionListener(e -> abrirListarNotificacoes());
        view.getAlterarSenha().addActionListener(e -> abrirAlterarSenha());
    }
    
    private boolean focarJanelaExistente(Class<? extends javax.swing.JInternalFrame> tipoView) {
        javax.swing.JInternalFrame[] frames = view.getDesktopPane().getAllFrames();
        for (javax.swing.JInternalFrame frame : frames) {
            if (tipoView.isInstance(frame)) {
                frame.toFront();
                try {
                    frame.setSelected(true);
                } catch (java.beans.PropertyVetoException e) {
                }
                return true;
            }
        }
        return false;
    }
    
    private void abrirControleUsuarios() {
        if (focarJanelaExistente(ControleUsuariosView.class)) {
            return;
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
        String info = String.format("Usuário: %s | Perfil: %s", 
                                    usuarioAutenticado.getNome(), 
                                    usuarioAutenticado.getTipo());
        view.setRodapeInfo(info);
        
        JMenuItem cadastrarUsuarioItem = view.getSaveAsMenuItem();
        
        view.getAlterarSenha().setVisible(true);
        view.getListarNotificacao().setVisible(true);
        
        if (usuarioAutenticado.getTipo().equals("Administrador") || usuarioAutenticado.getTipo().equals("AdiministradorChefe")) {
            cadastrarUsuarioItem.setVisible(true);
            view.getControleUsuariosMenuItem().setVisible(true);
            view.getConfgMenu().setVisible(true);
            view.getEnviarNotificacao().setVisible(true);
        } else {
            cadastrarUsuarioItem.setVisible(false);
            view.getConfgMenu().setVisible(false);
            view.getEnviarNotificacao().setVisible(false);
            view.getControleUsuariosMenuItem().setVisible(false);
        }
        
        view.getDeslogarMenuItem().setVisible(true);
        view.getFazerLoginMenuItem().setVisible(false);
    }
    
    private void abrirEnviarNotificacao() {
        if (focarJanelaExistente(EnviarNotificacaoView.class)) {
            return;
        }
        
        EnviarNotificacaoView enviarView = new EnviarNotificacaoView();
        new EnviarNotificacaoPresenter(
            enviarView,
            notificacaoRepository,
            usuarioRepository,
            usuarioAutenticado
        );
        view.getDesktopPane().add(enviarView);
        enviarView.setVisible(true);
        enviarView.setLocation(50, 50);
    }
    
    private void abrirListarNotificacoes() {
        if (focarJanelaExistente(ListarNotificacoesView.class)) {
            return;
        }
        
        ListarNotificacoesView listarView = new ListarNotificacoesView();
        new ListarNotificacoesPresenter(
            listarView,
            notificacaoRepository,
            usuarioRepository,
            usuarioAutenticado
        );
        view.getDesktopPane().add(listarView);
        listarView.setVisible(true);
        listarView.setLocation(50, 50);
    }
    
    private void abrirAlterarSenha() {
        if (focarJanelaExistente(AlterarSenhaView.class)) {
            return;
        }
        
        view.abrirAlterarSenhaView(usuarioAutenticado);
    }
}