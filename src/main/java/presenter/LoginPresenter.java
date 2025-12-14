package presenter;

import com.mycompany.acessousuario.model.Usuario;
import com.mycompany.logging.LogManager;
import java.sql.SQLException;
import repository.IUsuarioRepository;
import view.LoginView;
import view.MainMDIView;

/**
 *
 * @author marqu
 */
public class LoginPresenter {
    
    private final LoginView view;
    private final IUsuarioRepository repository;
    
    public LoginPresenter(LoginView view, IUsuarioRepository repository){
        this.view = view;
        this.repository = repository;
    }
    
    
    public void processarLogin(){
        String login = view.getLogin();
        String senha = view.getSenha();
        
        if(login.isEmpty() || senha.isEmpty()){
            // Log de falha - campos vazios
            LogManager.getInstance().logarFalha("LOGIN", "N/A", "Usuário e senha são obrigatórios", "N/A");
            view.exibirMensagem("Usuario e senha são obrigatorios.");
            return;
        }
        
        Usuario usuarioAutenticado = null;
        try{
            //autenticar no banco
            usuarioAutenticado = repository.autenticar(login,senha);
            
            if(usuarioAutenticado == null){
                // Log de falha - credenciais inválidas
                LogManager.getInstance().logarFalha("LOGIN", login, "Usuário ou senha inválidos", "N/A");
                view.exibirMensagem("Usurio ou senha inválidos.");
                return;
            }
            
            //verificar ativo
            if(!usuarioAutenticado.isAtivo()){
                // Log de falha - usuário não autorizado
                LogManager.getInstance().logarFalha("LOGIN", usuarioAutenticado.getNome(), "Acesso não autorizado pelo administrador", login);
                view.exibirMensagem("Seu acesso ainda não foi autorizado pelo administrador.");
                return;
            }
            
            // Log de sucesso - login bem-sucedido
            LogManager.getInstance().logarOperacao("LOGIN", usuarioAutenticado.getNome(), login);
            
            //login deu certo
            view.exibirMensagem("Login bem-sucedido!\n Bem Vindo(a), " + usuarioAutenticado.getNome());
            
            abrirTelaPrincipal(usuarioAutenticado);
            view.dispose();
        } catch(SQLException e){
            // Log de falha - erro de banco de dados
            LogManager.getInstance().logarFalha("LOGIN", login, "Erro ao acessar banco de dados: " + e.getMessage(), "N/A");
            view.exibirMensagem("Erro ao acessar o banco de dados" + e.getMessage());
        }
    }
    
    private void abrirTelaPrincipal(Usuario usuarioAutenticado) {
        MainMDIView mainView = MainMDIView.getInstance();
        new MainMDIViewPresenter(mainView, usuarioAutenticado, repository); 
        mainView.setVisible(true);
        
        // Mostrar item de deslogar e esconder "Fazer Login" após login bem-sucedido
        mainView.getDeslogarMenuItem().setVisible(true);
        mainView.getFazerLoginMenuItem().setVisible(false);
    }
}
