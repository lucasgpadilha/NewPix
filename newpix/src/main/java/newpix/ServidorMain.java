package newpix;

import newpix.views.ServerFrame;
import javax.swing.JOptionPane;

public class ServidorMain {
    public static void main(String[] args) {
        String portStr = JOptionPane.showInputDialog(null, "Digite a porta para iniciar o servidor:", "Iniciar Servidor", JOptionPane.QUESTION_MESSAGE);
        if (portStr != null && !portStr.trim().isEmpty()) {
            try {
                int port = Integer.parseInt(portStr);
                ServerFrame serverFrame = new ServerFrame();
                
                serverFrame.setPort(port);
                
                serverFrame.setVisible(true);
                
                new Thread(() -> {
                    Servidor servidor = new Servidor(serverFrame);
                    servidor.start(port);
                }).start();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Porta inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}