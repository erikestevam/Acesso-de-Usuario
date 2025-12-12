package presenter;

import com.mycompany.logging.LogManager;
import com.mycompany.acessousuario.model.Notificacao;
import com.mycompany.acessousuario.model.Usuario;
import java.sql.SQLException;
import java.util.List;
import repository.IUsuarioRepository;
import repository.INotificacaoRepository;
import view.ListarNotificacoesView;
import view.MainMDIView;

/**
 *
 * @author marqu
 */
public class ListarNotificacoesPresenter {
    
    private final ListarNotificacoesView view;
    private final INotificacaoRepository notificacaoRepository;
    private final IUsuarioRepository usuarioRepository;
    private final Usuario usuarioLogado;
    private List<Notificacao> notificacoes;
    private int indiceAtual = -1;
    
    public ListarNotificacoesPresenter(ListarNotificacoesView view,
                                      INotificacaoRepository notificacaoRepository,
                                      IUsuarioRepository usuarioRepository,
                                      Usuario usuarioLogado) {
        this.view = view;
        this.notificacaoRepository = notificacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioLogado = usuarioLogado;
        
        carregarNotificacoes();
        adicionarListeners();
    }
    
    private void adicionarListeners() {
        view.getCbLida().addActionListener(e -> marcarComoLida());
    }
    
    private void carregarNotificacoes() {
        try {
            notificacoes = notificacaoRepository.listarPorUsuario(usuarioLogado.getId());
            
            if (notificacoes.isEmpty()) {
                view.getLblRemetente().setText("Remetente: Nenhuma notificação");
                view.getLblMensagem().setText("Mensagem: Nenhuma notificação recebida");
                view.getCbLida().setEnabled(false);
                view.getCbLida().setSelected(false);
            } else {
                mostrarPrimeiraNotificacao();
            }
            
        } catch (SQLException e) {
            LogManager.getInstance().logarFalha(
                "LISTAR_NOTIFICACOES",
                e.getMessage(),
                usuarioLogado.getNome(),
                usuarioLogado.getLogin()
            );
            view.exibirMensagem("Erro ao carregar notificações: " + e.getMessage());
        }
    }
    
    private void mostrarPrimeiraNotificacao() {
        if (notificacoes != null && !notificacoes.isEmpty()) {
            indiceAtual = 0;
            mostrarNotificacao(notificacoes.get(0));
        }
    }
    
    private void mostrarNotificacao(Notificacao notificacao) {
        try {
            Usuario remetente = usuarioRepository.buscarPorId(notificacao.getIdRemetente());
            String nomeRemetente = remetente != null ? remetente.getNome() : "Desconhecido";
            
            view.getLblRemetente().setText("Remetente: " + nomeRemetente + " (" + notificacao.getDataEnvio() + ")");
            view.getLblMensagem().setText("Mensagem: " + notificacao.getConteudo());
            view.getCbLida().setSelected(notificacao.isLida());
            view.getCbLida().setEnabled(true);
            
        } catch (SQLException e) {
            view.exibirMensagem("Erro ao carregar dados do remetente: " + e.getMessage());
        }
    }
    
    private void marcarComoLida() {
        if (indiceAtual >= 0 && indiceAtual < notificacoes.size()) {
            Notificacao notificacao = notificacoes.get(indiceAtual);
            
            if (view.getCbLida().isSelected() && !notificacao.isLida()) {
                try {
                    notificacaoRepository.marcarComoLida(notificacao.getId());
                    notificacao.setLida(true);
                    
                    LogManager.getInstance().logarOperacao(
                        "MARCAR_NOTIFICACAO_LIDA",
                        "Notificação ID: " + notificacao.getId(),
                        usuarioLogado.getLogin()
                    );
                    
                    // Atualizar contador no rodapé
                    atualizarContadorRodape();
                    
                } catch (SQLException e) {
                    LogManager.getInstance().logarFalha(
                        "MARCAR_NOTIFICACAO_LIDA",
                        e.getMessage(),
                        "Notificação ID: " + notificacao.getId(),
                        usuarioLogado.getLogin()
                    );
                    view.exibirMensagem("Erro ao marcar notificação como lida: " + e.getMessage());
                    view.getCbLida().setSelected(false);
                }
            }
        }
    }
    
    // Métodos para navegação (podem ser adicionados botões na view depois)
    public void proximaNotificacao() {
        if (notificacoes != null && indiceAtual < notificacoes.size() - 1) {
            indiceAtual++;
            mostrarNotificacao(notificacoes.get(indiceAtual));
        }
    }
    
    public void notificacaoAnterior() {
        if (notificacoes != null && indiceAtual > 0) {
            indiceAtual--;
            mostrarNotificacao(notificacoes.get(indiceAtual));
        }
    }
    
    public void atualizarLista() {
        carregarNotificacoes();
    }
    
    private void atualizarContadorRodape() {
        try {
            MainMDIView mainView = MainMDIView.getInstance();
            if (mainView != null) {
                int naoLidas = notificacaoRepository.contarNaoLidas(usuarioLogado.getId());
                mainView.atualizarContadorNotificacoes(naoLidas);
            }
        } catch (SQLException e) {
            // Ignorar erro ao atualizar contador
        }
    }
}

