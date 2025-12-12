/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presenter;

import com.mycompany.logging.LogManager;
import com.mycompany.acessousuario.model.Usuario;
import com.pss.senha.validacao.ValidadorSenha;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import repository.IUsuarioRepository;
import view.AlterarSenhaView;

/**
 *
 * @author marqu
 */
public class AlterarSenhaPresenter {
    
    private final AlterarSenhaView view;
    private final IUsuarioRepository usuarioRepository;
    private final Usuario usuarioLogado; // O usuário que está alterando a senha

    public AlterarSenhaPresenter(AlterarSenhaView view, IUsuarioRepository uRepo, Usuario usuarioLogado) {
        this.view = view;
        this.usuarioRepository = uRepo;
        this.usuarioLogado = usuarioLogado;
        
        adicionarListeners();
    }

    private void adicionarListeners() {
        javax.swing.JButton confirmarBotao = view.getJbConfirmar();
    
        // Remover os listenes anterior
        for (java.awt.event.ActionListener al : confirmarBotao.getActionListeners()) {
            confirmarBotao.removeActionListener(al);
        }

        // 2. ADICIONA O NOSSO PRÓPRIO LISTENER (que executa a lógica)
        confirmarBotao.addActionListener(e -> salvarNovaSenha());
    }
    
    private void salvarNovaSenha() {
        String novaSenha = new String(view.getNovaSenha().getPassword());
        String confirmaSenha = new String(view.getConfNovaSenha().getPassword());
        
        // 1. Validação
        if (novaSenha.isEmpty() || confirmaSenha.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Todos os campos de senha são obrigatórios.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!novaSenha.equals(confirmaSenha)) {
            JOptionPane.showMessageDialog(view, "A nova senha e a confirmação não coincidem.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validação de senha forte usando ValidadorSenha (RNF09 - item 7)
        ValidadorSenha validadorSenha = new ValidadorSenha();
        List<String> errosSenha = validadorSenha.validar(novaSenha);
        if (!errosSenha.isEmpty()) {
            String errosFormatados = String.join("\n", errosSenha);
            JOptionPane.showMessageDialog(view, 
                "Senha inválida:\n" + errosFormatados, 
                "Erro de Validação", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // RNF: A nova senha deve ser diferente da senha anterior? 
        // Não é um requisito obrigatório, mas é boa prática. Vamos ignorar por agora.
        
        try {
            // 2. Persistência
            usuarioRepository.atualizarSenha(usuarioLogado.getId(), novaSenha);
            
            // 3. Log de sucesso
            LogManager.getInstance().logarOperacao("ALTERACAO_SENHA", usuarioLogado.getNome(), usuarioLogado.getLogin());
            
            // 4. Sucesso e Fechamento
            JOptionPane.showMessageDialog(view, "Senha alterada com sucesso! Você precisará logar novamente.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            view.fechar();
            
            // RNF: Aqui você deve invalidar a sessão e forçar o login novamente.
            // Ex: mainView.abrirLogin();
            
        } catch (SQLException e) {
            // Log de falha
            LogManager.getInstance().logarFalha("ALTERACAO_SENHA", usuarioLogado.getNome(), e.getMessage(), usuarioLogado.getLogin());
            JOptionPane.showMessageDialog(view, "Erro ao salvar a nova senha: " + e.getMessage(), "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }
}
