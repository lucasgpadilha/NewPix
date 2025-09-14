package newpix;

import newpix.views.HomeFrame;
import newpix.views.ServerFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Opção para iniciar Servidor ou Cliente
        Object[] options = {"Iniciar Servidor", "Iniciar Cliente NewPix"};
        int choice = JOptionPane.showOptionDialog(null, "O que você gostaria de iniciar?",
                "NewPix Launcher", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) { // Iniciar Servidor
            String portStr = JOptionPane.showInputDialog(null, "Digite a porta para o servidor:", "Iniciar Servidor", JOptionPane.QUESTION_MESSAGE);
            if (portStr != null && !portStr.trim().isEmpty()) {
                try {
                    int port = Integer.parseInt(portStr);
                    ServerFrame serverFrame = new ServerFrame();
                    serverFrame.setVisible(true);
                    new Thread(() -> {
                        Servidor servidor = new Servidor(serverFrame);
                        servidor.start(port);
                    }).start();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Porta inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (choice == 1) { // Iniciar Cliente
            SwingUtilities.invokeLater(() -> {
                HomeFrame homeFrame = new HomeFrame();
                homeFrame.setVisible(true);
            });
        }
    }
}