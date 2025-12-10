/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presenter;

import com.mycompany.acessousuario.model.Usuario;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import repository.INotificacaoRepository;
import repository.IUsuarioRepository;
import view.EditarUsuarioView;
import view.ListarUsuariosView;
import view.MainMDIView;

/**
 *
 * @author marqu
 */
public class ListarUsuariosPresenter {
    private final ListarUsuariosView view;
    private final IUsuarioRepository usuarioRepository;
    private final INotificacaoRepository notificacaoRepository;
    private final Usuario usuarioLogado;

  
    public ListarUsuariosPresenter(ListarUsuariosView view, 
                                 IUsuarioRepository uRepo, 
                                 INotificacaoRepository nRepo,
                                 Usuario usuarioLogado) {
        this.view = view;
        this.usuarioRepository = uRepo;
        this.notificacaoRepository = nRepo;
        this.usuarioLogado = usuarioLogado;
        
        carregarUsuarios();
        adicionarListeners();
    }
    
    private void adicionarListeners() {
    view.getJbEditar().addActionListener(e -> editarUsuario());
}

    public void carregarUsuarios() {
        DefaultTableModel model = (DefaultTableModel) view.getJTable().getModel();
        model.setRowCount(0); // Limpa as linhas existentes antes de carregar
        
        try {
            
            List<Usuario> usuarios = usuarioRepository.listarTodos();
            
            for (Usuario u : usuarios) {
                int notificacoesEnviadas = notificacaoRepository.contarEnviadasPeloUsuario(u.getId());
                int notificacoesLidas = notificacaoRepository.contarNotificacoesLidas(u.getId());
                            
                model.addRow(new Object[]{
                    u.getNome(),
                    u.getData_criacao(),
                    notificacoesEnviadas,  
                    notificacoesLidas      
                });
            }
            
            view.getJTable().setModel(model);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Erro ao carregar lista de usuários: " + e.getMessage(), "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void editarUsuario(){
        String loginUsuario = view.getUsuarioEditar().getText(); 
    
        if (loginUsuario.isEmpty()) {
            view.exibirMensagem("Nenhum usuário foi informado.");
            return;
        }

        try {
            Usuario usuarioParaEditar = usuarioRepository.buscarPorLogin(loginUsuario);

            if (usuarioParaEditar == null) {
                view.exibirMensagem("Usuário com login/nome '" + loginUsuario + "' não encontrado.");
                return;
            }

            abrirEdicaoView(usuarioParaEditar, usuarioLogado);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Erro ao buscar dados do usuário para edição: " + e.getMessage(), "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirEdicaoView(Usuario usuario, Usuario usuarioLogado) {
        MainMDIView mdi = MainMDIView.getInstance();
        EditarUsuarioView editarView = new EditarUsuarioView(usuario, usuarioLogado);
        mdi.getDesktopPane().add(editarView);
        editarView.setVisible(true);
    }
}