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
import view.ListarUsuariosView;

/**
 *
 * @author marqu
 */
public class ListarUsuariosPresenter {
    private final ListarUsuariosView view;
    private final IUsuarioRepository usuarioRepository;
    private final INotificacaoRepository notificacaoRepository;

    public ListarUsuariosPresenter(ListarUsuariosView view, 
                                 IUsuarioRepository uRepo, 
                                 INotificacaoRepository nRepo) {
        this.view = view;
        this.usuarioRepository = uRepo;
        this.notificacaoRepository = nRepo;
        
        carregarUsuarios();
        adicionarListeners();
    }
    
    private void adicionarListeners() {
        // Funcionalidade de edição movida para ControleUsuariosView
        // O botão de editar pode ser removido ou desabilitado
        view.getJbEditar().setEnabled(false);
        view.getJbEditar().setToolTipText("Use a tela 'Controle de Usuarios' para editar usuários");
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
    
    // Funcionalidade de edição removida - agora está em ControleUsuariosView
}