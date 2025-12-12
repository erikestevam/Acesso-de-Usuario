package presenter;

import com.mycompany.logging.LogManager;
import com.mycompany.acessousuario.model.Notificacao;
import com.mycompany.acessousuario.model.Usuario;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import repository.IUsuarioRepository;
import repository.INotificacaoRepository;
import view.EnviarNotificacaoView;
import view.MainMDIView;

/**
 *
 * @author marqu
 */
public class EnviarNotificacaoPresenter {
    
    private final EnviarNotificacaoView view;
    private final INotificacaoRepository notificacaoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final Usuario usuarioRemetente;
    
    public EnviarNotificacaoPresenter(EnviarNotificacaoView view,
                                     INotificacaoRepository notificacaoRepository,
                                     IUsuarioRepository usuarioRepository,
                                     Usuario usuarioRemetente) {
        this.view = view;
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioRemetente = usuarioRemetente;
        
        adicionarListeners();
    }
    
    private void adicionarListeners() {
        view.getBtnEnviar().addActionListener(e -> enviarNotificacao());
    }
    
    private void enviarNotificacao() {
        String loginDestinatario = view.getTfDestinatario().getText().trim();
        String mensagem = view.getTaMensagem().getText().trim();
        
        // Validações
        if (loginDestinatario.isEmpty()) {
            LogManager.getInstance().logarFalha(
                "ENVIO_NOTIFICACAO",
                "Login do destinatário não informado",
                loginDestinatario,
                usuarioRemetente.getLogin()
            );
            view.exibirMensagem("Por favor, informe o login do destinatário.");
            return;
        }
        
        if (mensagem.isEmpty()) {
            LogManager.getInstance().logarFalha(
                "ENVIO_NOTIFICACAO",
                "Mensagem não informada",
                loginDestinatario,
                usuarioRemetente.getLogin()
            );
            view.exibirMensagem("Por favor, informe a mensagem da notificação.");
            return;
        }
        
        // Verificar se o destinatário existe
        try {
            Usuario destinatario = usuarioRepository.buscarPorLogin(loginDestinatario);
            
            if (destinatario == null) {
                LogManager.getInstance().logarFalha(
                    "ENVIO_NOTIFICACAO",
                    "Destinatário não encontrado: " + loginDestinatario,
                    loginDestinatario,
                    usuarioRemetente.getLogin()
                );
                view.exibirMensagem("Usuário com login '" + loginDestinatario + "' não encontrado.");
                return;
            }
            
            if (!destinatario.isAtivo()) {
                LogManager.getInstance().logarFalha(
                    "ENVIO_NOTIFICACAO",
                    "Destinatário inativo: " + loginDestinatario,
                    loginDestinatario,
                    usuarioRemetente.getLogin()
                );
                view.exibirMensagem("Não é possível enviar notificação para usuário inativo.");
                return;
            }
            
            // Criar e salvar notificação
            String dataEnvio = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            Notificacao notificacao = new Notificacao(
                usuarioRemetente.getId(),
                destinatario.getId(),
                mensagem,
                dataEnvio
            );
            
            notificacaoRepository.salvar(notificacao);
            
            // Log de sucesso
            LogManager.getInstance().logarOperacao(
                "ENVIO_NOTIFICACAO",
                destinatario.getNome(),
                usuarioRemetente.getLogin()
            );
            
            view.exibirMensagem("Notificação enviada com sucesso para " + destinatario.getNome() + "!");
            
            // Limpar campos
            view.getTfDestinatario().setText("");
            view.getTaMensagem().setText("");
            
        } catch (SQLException e) {
            LogManager.getInstance().logarFalha(
                "ENVIO_NOTIFICACAO",
                e.getMessage(),
                loginDestinatario,
                usuarioRemetente.getLogin()
            );
            view.exibirMensagem("Erro ao enviar notificação: " + e.getMessage());
        }
    }
    
    private void atualizarContadorRodape() {
        try {
            MainMDIView mainView = MainMDIView.getInstance();
            if (mainView != null) {
                // Atualizar contador para todos os usuários logados seria complexo
                // Por enquanto, apenas atualizamos se o remetente estiver logado
                // O contador será atualizado quando o destinatário abrir a lista de notificações
            }
        } catch (Exception e) {
            // Ignorar erro ao atualizar contador
        }
    }
}

