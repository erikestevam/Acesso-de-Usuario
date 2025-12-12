/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presenter;

import com.mycompany.acessousuario.model.Usuario;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import repository.IUsuarioRepository;
import view.EditarUsuarioView;

/**
 *
 * @author marqu
 */
public class EditarUsuarioPresenter {
    
    private final EditarUsuarioView view;
    private final IUsuarioRepository usuarioRepository; 
    private final Usuario usuarioAtual; 
    private final Usuario usuarioLogado;

    public EditarUsuarioPresenter(EditarUsuarioView view, IUsuarioRepository uRepo, Usuario usuario, Usuario usuarioLogado) {
        this.view = view;
        this.usuarioRepository = uRepo;
        this.usuarioAtual = usuario;
        this.usuarioLogado = usuarioLogado;
        
        permissoes();
        carregarDadosUsuario();
        adicionarListeners();
    }
    
    private void permissoes(){
        if(!usuarioLogado.getTipo().equals("AdiministradorChefe")){
            view.getJcTipo().setEnabled(false);
        }
        if(usuarioLogado.getId() == usuarioAtual.getId()){
            view.getJcTipo().setEnabled(false);
        }
    }
    
    private void adicionarListeners() {
        view.getJbEditar().addActionListener(e -> editarUsuario());
    }
    
    private void carregarDadosUsuario() {
        view.getTfUsuario().setText(usuarioAtual.getLogin());
        view.getTfSenha().setText(usuarioAtual.getSenha());
        view.getJcTipo().setSelectedItem(usuarioAtual.getTipo());
        view.getJcStatus().setSelectedItem(usuarioAtual.isAtivo() ? "Ativo" : "Não Ativo"); 
    }
    
    public void editarUsuario() {
        String novoLogin = view.getTfUsuario().getText().trim();
        String novaSenha = view.getTfSenha().getText().trim();
        String novoTipo = view.getJcTipo().getSelectedItem().toString();
        String novoStatusStr = view.getJcStatus().getSelectedItem().toString();

        // Converte o Status (Ativo/Não Ativo) para boolean ou int (1/0)
        boolean novoStatus = novoStatusStr.equals("Ativo");

        if (novoLogin.isEmpty()) {
            JOptionPane.showMessageDialog(view, "O login do usuário não pode ser vazio.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        usuarioAtual.setLogin(novoLogin); 

        if (!novaSenha.isEmpty()) {
            usuarioAtual.setSenha(novaSenha);
        }

        usuarioAtual.setTipo(novoTipo);
        usuarioAtual.setAtivo(novoStatus);

        try {
            usuarioRepository.atualizarUsuario(usuarioAtual); 

            JOptionPane.showMessageDialog(view, "Dados do usuário '" + usuarioAtual.getLogin() + "' atualizados com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            view.dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Erro ao atualizar dados no banco de dados: " + e.getMessage(), "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }
}
