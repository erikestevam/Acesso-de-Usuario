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
import repository.IUsuarioRepository;
import view.CadastroUsuarioView;

/**
 *
 * @author marqu
 */
public class CadastroUsuarioPresenter {
    
    public final CadastroUsuarioView view;
    public final IUsuarioRepository repository;
    
    public CadastroUsuarioPresenter(CadastroUsuarioView view,IUsuarioRepository repository){
        this.view = view;
        this.repository = repository;
    }
    
    public void processarCadastro(){
        String login = view.getLogin().trim();
        String senha = view.getSenha().trim();
        String confirmaSenha = view.getConfirmaSenha().trim();
        
        //validar dados
        if(login.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()){
            // Log de falha - campos vazios
            String usuarioLog = "SISTEMA";
            try {
                List<Usuario> usuarioExistentes = repository.listarTodos();
                usuarioLog = usuarioExistentes.isEmpty() ? "SISTEMA" : "NOVO_USUARIO";
            } catch (SQLException ex) {}
            LogManager.getInstance().logarFalha("INCLUSAO_USUARIO", login.isEmpty() ? "N/A" : login, "Campos obrigatórios não preenchidos", usuarioLog);
            view.exibirMensagem("Todos os campos devem ser preenchidos.");
            return;
        }
        
        if(!senha.equals(confirmaSenha)){
            // Log de falha - senhas não coincidem
            String usuarioLog = "SISTEMA";
            try {
                List<Usuario> usuarioExistentes = repository.listarTodos();
                usuarioLog = usuarioExistentes.isEmpty() ? "SISTEMA" : "NOVO_USUARIO";
            } catch (SQLException ex) {}
            LogManager.getInstance().logarFalha("INCLUSAO_USUARIO", login, "Senha e confirmação não coincidem", usuarioLog);
            view.exibirMensagem("A senha e a confirmação de senha não coincidem.");
            return;
        }

        // validação de senha com biblioteca externa
        ValidadorSenha validadorSenha = new ValidadorSenha();
        List<String> errosSenha = validadorSenha.validar(senha);
        if(!errosSenha.isEmpty()){
            String errosFormatados = String.join("\n", errosSenha);
            
            // Log de falha - senha inválida
            String usuarioLog = "SISTEMA";
            try {
                List<Usuario> usuarioExistentes = repository.listarTodos();
                usuarioLog = usuarioExistentes.isEmpty() ? "SISTEMA" : "NOVO_USUARIO";
            } catch (SQLException ex) {}
            LogManager.getInstance().logarFalha("INCLUSAO_USUARIO", login, "Senha inválida: " + errosFormatados, usuarioLog);
            
            view.exibirMensagem("Senha inválida:\n" + errosFormatados);
            return;
        } else {
            view.exibirMensagem("Senha validada com sucesso.");
        }
        
        try{
            //verificar se o login ja existe
            if(repository.verificarLoginExistente(login)){
                // Log de falha - login já existe
                String usuarioLog = "SISTEMA";
                try {
                    List<Usuario> usuarioExistentes = repository.listarTodos();
                    usuarioLog = usuarioExistentes.isEmpty() ? "SISTEMA" : "NOVO_USUARIO";
                } catch (SQLException ex) {}
                LogManager.getInstance().logarFalha("INCLUSAO_USUARIO", login, "Login já existe no sistema", usuarioLog);
                view.exibirMensagem("Este nome de usuário já está em uso.");
                return;
            }
            
            //verificar se é o primeiro usuario
            List<Usuario> usuarioExistentes = repository.listarTodos();
            
            String tipoUsuario;
            boolean ativo;
            
            if(usuarioExistentes.isEmpty()){
                tipoUsuario = "AdiministradorChefe";
                ativo = true;
                view.exibirMensagem("Cadastro realizado com sucesso! Você é o Administrador inicial.");
            }else{
                tipoUsuario = "Usuário";
                ativo = false;
                view.exibirMensagem("Solicitação de cadastro enviada. Seu acesso será liberado após a autorização do Administrador.");
            }
            
            Usuario novoUsuario = new Usuario(login, login, senha, "", tipoUsuario);
            novoUsuario.setAtivo(ativo);
            
            repository.salvar(novoUsuario);
            
            // Log de sucesso
            String usuarioLog = usuarioExistentes.isEmpty() ? "SISTEMA" : "NOVO_USUARIO";
            LogManager.getInstance().logarOperacao("INCLUSAO_USUARIO", novoUsuario.getNome(), usuarioLog);
            
            view.limparCampos();
            
        }catch (SQLException e) {
            // Log de falha
            String usuarioLog = "SISTEMA";
            try {
                List<Usuario> usuarioExistentes = repository.listarTodos();
                usuarioLog = usuarioExistentes.isEmpty() ? "SISTEMA" : "NOVO_USUARIO";
            } catch (SQLException ex) {}
            LogManager.getInstance().logarFalha("INCLUSAO_USUARIO", login, e.getMessage(), usuarioLog);
            view.exibirMensagem("Erro ao salvar no banco de dados: " + e.getMessage());
        }
    }
}
