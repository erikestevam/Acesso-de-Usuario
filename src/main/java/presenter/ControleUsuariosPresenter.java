/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package presenter;

import com.mycompany.acessousuario.log.LogManager;
import com.mycompany.acessousuario.model.Usuario;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import repository.INotificacaoRepository;
import repository.IUsuarioRepository;
import view.ControleUsuariosView;

/**
 *
 * @author marqu
 */
public class ControleUsuariosPresenter {
    
    private final ControleUsuariosView view;
    private final IUsuarioRepository usuarioRepository;
    private final INotificacaoRepository notificacaoRepository;
    private final Usuario usuarioAutenticado;
    
    public ControleUsuariosPresenter(ControleUsuariosView view,
                                    IUsuarioRepository uRepo,
                                    INotificacaoRepository nRepo,
                                    Usuario usuarioAutenticado) {
        this.view = view;
        this.usuarioRepository = uRepo;
        this.notificacaoRepository = nRepo;
        this.usuarioAutenticado = usuarioAutenticado;
        
        carregarUsuarios();
    }
    
    public void carregarUsuarios() {
        DefaultTableModel model = (DefaultTableModel) view.getJTable().getModel();
        model.setRowCount(0);
        
        try {
            List<Usuario> usuarios = usuarioRepository.listarTodos();
            
            // Aplicar filtros
            boolean mostrarPendentes = view.getJcbPendentes().isSelected();
            boolean mostrarAdmin = view.getJcbAdministradores().isSelected();
            
            for (Usuario u : usuarios) {
                // Filtro de pendentes
                if (mostrarPendentes && u.isAtivo()) {
                    continue;
                }
                
                // Filtro de administradores
                if (mostrarAdmin && !u.getTipo().equals("Administrador") && !u.getTipo().equals("AdiministradorChefe")) {
                    continue;
                }
                
                int notificacoesEnviadas = notificacaoRepository.contarEnviadasPeloUsuario(u.getId());
                int notificacoesLidas = notificacaoRepository.contarNotificacoesLidas(u.getId());
                
                String status = u.isAtivo() ? "Ativo" : "Pendente";
                
                model.addRow(new Object[]{
                    u.getId(),
                    u.getNome(),
                    u.getLogin(),
                    u.getTipo(),
                    status,
                    u.getData_criacao(),
                    notificacoesEnviadas,
                    notificacoesLidas
                });
            }
            
            view.getJTable().setModel(model);
            
        } catch (SQLException e) {
            view.exibirMensagem("Erro ao carregar lista de usuários: " + e.getMessage());
        }
    }
    
    public void aplicarFiltros() {
        carregarUsuarios();
    }
    
    private Usuario obterUsuarioSelecionado() {
        int linhaSelecionada = view.getJTable().getSelectedRow();
        if (linhaSelecionada == -1) {
            view.exibirMensagem("Selecione um usuário da tabela.");
            return null;
        }
        
        DefaultTableModel model = (DefaultTableModel) view.getJTable().getModel();
        int idUsuario = (Integer) model.getValueAt(linhaSelecionada, 0);
        
        try {
            return usuarioRepository.buscarPorId(idUsuario);
        } catch (SQLException e) {
            view.exibirMensagem("Erro ao buscar usuário: " + e.getMessage());
            return null;
        }
    }
    
    public void autorizarUsuario() {
        // Validar permissão
        if (!usuarioAutenticado.getTipo().equals("Administrador") && 
            !usuarioAutenticado.getTipo().equals("AdiministradorChefe")) {
            view.exibirMensagem("Apenas administradores podem autorizar usuários.");
            return;
        }
        
        Usuario usuario = obterUsuarioSelecionado();
        if (usuario == null) return;
        
        if (usuario.isAtivo()) {
            view.exibirMensagem("Este usuário já está autorizado.");
            return;
        }
        
        try {
            usuario.setAtivo(true);
            usuarioRepository.atualizar(usuario);
            
            LogManager.getInstance().logarOperacao(
                "AUTORIZACAO_USUARIO",
                usuario.getNome(),
                usuarioAutenticado.getLogin()
            );
            
            view.exibirMensagem("Usuário autorizado com sucesso!");
            carregarUsuarios();
            
        } catch (SQLException e) {
            LogManager.getInstance().logarFalha(
                "AUTORIZACAO_USUARIO",
                usuario.getNome(),
                e.getMessage(),
                usuarioAutenticado.getLogin()
            );
            view.exibirMensagem("Erro ao autorizar usuário: " + e.getMessage());
        }
    }
    
    public void recusarUsuario() {
        // Validar permissão
        if (!usuarioAutenticado.getTipo().equals("Administrador") && 
            !usuarioAutenticado.getTipo().equals("AdiministradorChefe")) {
            view.exibirMensagem("Apenas administradores podem recusar usuários.");
            return;
        }
        
        Usuario usuario = obterUsuarioSelecionado();
        if (usuario == null) return;
        
        int confirmacao = JOptionPane.showConfirmDialog(
            view,
            "Deseja realmente excluir o usuário " + usuario.getNome() + "?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                usuarioRepository.deletar(usuario.getId());
                
                LogManager.getInstance().logarOperacao(
                    "EXCLUSAO_USUARIO",
                    usuario.getNome(),
                    usuarioAutenticado.getLogin()
                );
                
                view.exibirMensagem("Usuário excluído com sucesso!");
                carregarUsuarios();
                
            } catch (SQLException e) {
                LogManager.getInstance().logarFalha(
                    "EXCLUSAO_USUARIO",
                    usuario.getNome(),
                    e.getMessage(),
                    usuarioAutenticado.getLogin()
                );
                view.exibirMensagem("Erro ao excluir usuário: " + e.getMessage());
            }
        }
    }
    
    public void promoverAdministrador() {
        // Validar permissão
        if (!usuarioAutenticado.getTipo().equals("Administrador") && 
            !usuarioAutenticado.getTipo().equals("AdiministradorChefe")) {
            view.exibirMensagem("Apenas administradores podem promover outros usuários.");
            return;
        }
        
        Usuario usuario = obterUsuarioSelecionado();
        if (usuario == null) return;
        
        // Não pode promover a si mesmo
        if (usuario.getId() == usuarioAutenticado.getId()) {
            view.exibirMensagem("Você não pode alterar seu próprio perfil.");
            return;
        }
        
        // Usuário deve estar ativo
        if (!usuario.isAtivo()) {
            view.exibirMensagem("Apenas usuários autorizados podem ser promovidos.");
            return;
        }
        
        // Já é administrador
        if (usuario.getTipo().equals("Administrador") || usuario.getTipo().equals("AdiministradorChefe")) {
            view.exibirMensagem("Este usuário já é administrador.");
            return;
        }
        
        try {
            usuario.setTipo("Administrador");
            usuarioRepository.atualizar(usuario);
            
            LogManager.getInstance().logarOperacao(
                "PROMOCAO_ADMINISTRADOR",
                usuario.getNome(),
                usuarioAutenticado.getLogin()
            );
            
            view.exibirMensagem("Usuário promovido a Administrador com sucesso!");
            carregarUsuarios();
            
        } catch (SQLException e) {
            LogManager.getInstance().logarFalha(
                "PROMOCAO_ADMINISTRADOR",
                usuario.getNome(),
                e.getMessage(),
                usuarioAutenticado.getLogin()
            );
            view.exibirMensagem("Erro ao promover usuário: " + e.getMessage());
        }
    }
    
    public void removerAdministrador() {
        // Validar: APENAS o primeiro admin pode remover admins
        if (!usuarioAutenticado.getTipo().equals("AdiministradorChefe")) {
            view.exibirMensagem("Apenas o Administrador Chefe pode remover outros administradores.");
            return;
        }
        
        Usuario usuario = obterUsuarioSelecionado();
        if (usuario == null) return;
        
        // Não pode remover a si mesmo
        if (usuario.getId() == usuarioAutenticado.getId()) {
            view.exibirMensagem("Você não pode alterar seu próprio perfil.");
            return;
        }
        
        // Só pode remover Administradores (não o Chefe)
        if (!usuario.getTipo().equals("Administrador")) {
            view.exibirMensagem("Apenas Administradores podem ser removidos deste cargo.");
            return;
        }
        
        int confirmacao = JOptionPane.showConfirmDialog(
            view,
            "Deseja remover " + usuario.getNome() + " do cargo de Administrador?",
            "Confirmar Remoção",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                usuario.setTipo("Usuário");
                usuarioRepository.atualizar(usuario);
                
                LogManager.getInstance().logarOperacao(
                    "REMOCAO_ADMINISTRADOR",
                    usuario.getNome(),
                    usuarioAutenticado.getLogin()
                );
                
                view.exibirMensagem("Administrador removido com sucesso!");
                carregarUsuarios();
                
            } catch (SQLException e) {
                LogManager.getInstance().logarFalha(
                    "REMOCAO_ADMINISTRADOR",
                    usuario.getNome(),
                    e.getMessage(),
                    usuarioAutenticado.getLogin()
                );
                view.exibirMensagem("Erro ao remover administrador: " + e.getMessage());
            }
        }
    }
}



