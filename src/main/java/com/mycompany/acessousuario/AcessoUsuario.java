package com.mycompany.acessousuario;

import com.mycompany.acessousuario.dao.ConfiguracaoDAO;
import com.mycompany.acessousuario.dao.DatabaseSetup;
import com.mycompany.acessousuario.log.LogManager;
import repository.IConfiguracaoRepository;
import view.MainMDIView;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author erik
 */
public class AcessoUsuario {

    public static void main(String[] args) {
        // Inicializa banco de dados
        DatabaseSetup.inicializarBanco();
        
        // Inicializa sistema de log com formato salvo ou padrão
        try {
            IConfiguracaoRepository configRepo = new ConfiguracaoDAO();
            String formatoLog = configRepo.obterValor("formato_log");
            if (formatoLog == null || formatoLog.isEmpty()) {
                formatoLog = "CSV"; // padrão
            }
            LogManager.getInstance().definirFormato(formatoLog);
        } catch (Exception e) {
            System.err.println("Erro ao inicializar log: " + e.getMessage());
        }
        
        /* Replica o setup de look-and-feel do MainMDIView.main */
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            // mantém LAF padrão caso falhe
        }

        /* Garante criação na EDT, como o main original de MainMDIView */
        java.awt.EventQueue.invokeLater(() -> {
            MainMDIView mainView = MainMDIView.getInstance();
            mainView.setVisible(true);
            mainView.setLocationRelativeTo(null); // Centraliza a janela
            mainView.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH); // Maximiza para melhor visualização
        });
    }
}
