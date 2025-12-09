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
    
    private void abrirAlterarSenha() {
        MainMDIView mdi = MainMDIView.getInstance(); 

        AlterarSenhaView alterarSenhaView = new AlterarSenhaView();

        IUsuarioRepository uRepo = new UsuarioDAO(); // Injetar a dependência concreta
        new AlterarSenhaPresenter(alterarSenhaView, uRepo, usuarioAutenticado);

        mdi.getDesktopPane().add(alterarSenhaView);
        alterarSenhaView.setVisible(true);
    }
}