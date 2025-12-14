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
    private final Usuario usuarioLogado;

    public AlterarSenhaPresenter(AlterarSenhaView view, IUsuarioRepository uRepo, Usuario usuarioLogado) {
        this.view = view;
        this.usuarioRepository = uRepo;
        this.usuarioLogado = usuarioLogado;
        
        adicionarListeners();
    }

    private void adicionarListeners() {
        javax.swing.JButton confirmarBotao = view.getJbConfirmar();
    
        for (java.awt.event.ActionListener al : confirmarBotao.getActionListeners()) {
            confirmarBotao.removeActionListener(al);
        }

        confirmarBotao.addActionListener(e -> salvarNovaSenha());
    }
    
    private void salvarNovaSenha() {
        String novaSenha = new String(view.getNovaSenha().getPassword());
        String confirmaSenha = new String(view.getConfNovaSenha().getPassword());
        
        if (novaSenha.isEmpty() || confirmaSenha.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Todos os campos de senha são obrigatórios.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!novaSenha.equals(confirmaSenha)) {
            JOptionPane.showMessageDialog(view, "A nova senha e a confirmação não coincidem.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
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
        
        try {
            usuarioRepository.atualizarSenha(usuarioLogado.getId(), novaSenha);
            
            LogManager.getInstance().logarOperacao("ALTERACAO_SENHA", usuarioLogado.getNome(), usuarioLogado.getLogin());
            
            JOptionPane.showMessageDialog(view, "Senha alterada com sucesso! Você precisará logar novamente.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            view.fechar();
            
        } catch (SQLException e) {
            LogManager.getInstance().logarFalha("ALTERACAO_SENHA", usuarioLogado.getNome(), e.getMessage(), usuarioLogado.getLogin());
            JOptionPane.showMessageDialog(view, "Erro ao salvar a nova senha: " + e.getMessage(), "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }
}
