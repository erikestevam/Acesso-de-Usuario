package presenter;

import com.mycompany.logging.LogManager;
import java.io.IOException;
import repository.IConfiguracaoRepository;
import view.ConfiguracaoView;

/**
 *
 * @author marqu
 */
public class ConfiguracaoPresenter {
    
    private final ConfiguracaoView view;
    private final IConfiguracaoRepository configRepository;
    
    public ConfiguracaoPresenter(ConfiguracaoView view, IConfiguracaoRepository configRepository) {
        this.view = view;
        this.configRepository = configRepository;
        
        carregarFormatoAtual();
        adicionarListeners();
    }
    
    private void carregarFormatoAtual() {
        try {
            String formatoAtual = configRepository.obterValor("formato_log");
            if (formatoAtual == null || formatoAtual.isEmpty()) {
                formatoAtual = "CSV"; // Padrão
            }
            
            // Ajustar para o valor correto do ComboBox (JSONL ao invés de JSON)
            if ("JSONL".equalsIgnoreCase(formatoAtual)) {
                view.getCbFormato().setSelectedItem("JSONL");
            } else {
                view.getCbFormato().setSelectedItem("CSV");
            }
            
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar configuração: " + e.getMessage());
        }
    }
    
    private void adicionarListeners() {
        view.getBtnSalvar().addActionListener(e -> salvarConfiguracao());
    }
    
    private void salvarConfiguracao() {
        String formatoSelecionado = (String) view.getCbFormato().getSelectedItem();
        
        // Converter "JSONL" se necessário (o ComboBox pode ter "JSON")
        String formatoParaSalvar = "JSONL".equalsIgnoreCase(formatoSelecionado) ? "JSONL" : "CSV";
        
        try {
            // Salvar no banco
            configRepository.salvarValor("formato_log", formatoParaSalvar);
            
            // Atualizar LogManager
            LogManager.getInstance().definirFormato(formatoParaSalvar);
            
            view.exibirMensagem("Configuração salva com sucesso! Formato de log alterado para " + formatoParaSalvar + ".");
            
        } catch (IOException e) {
            view.exibirMensagem("Erro ao alterar formato de log: " + e.getMessage());
        } catch (Exception e) {
            view.exibirMensagem("Erro ao salvar configuração: " + e.getMessage());
        }
    }
}

