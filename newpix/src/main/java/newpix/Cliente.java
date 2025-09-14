package newpix;

import javax.swing.SwingUtilities;
import newpix.views.LoginFrame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    // --- Início da implementação do Singleton ---
    private static Cliente instance;

    private Cliente() {} // Construtor privado para evitar instanciação externa

    public static synchronized Cliente getInstance() {
        if (instance == null) {
            instance = new Cliente();
        }
        return instance;
    }
    // --- Fim da implementação do Singleton ---

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private LoginFrame loginFrame; // Referência à tela de login para atualizar a UI

    public void setLoginFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    public void startConnection(String ip, int port) throws IOException {
        if (isConnected()) {
            return; // Já está conectado
        }
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        new Thread(this::listenToServer).start();
    }

    public void sendMessage(String msg) {
        if (isConnected()) {
            out.println(msg);
        } else {
            System.err.println("Não conectado. Não é possível enviar a mensagem.");
        }
    }

    private void listenToServer() {
        try {
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Resposta do Servidor: " + fromServer);
                // Usar SwingUtilities para atualizar a GUI de forma segura
                final String serverResponse = fromServer;
                SwingUtilities.invokeLater(() -> {
                    if (loginFrame != null) {
                        loginFrame.handleServerResponse(serverResponse);
                    }
                    // Adicionar lógica para outras telas aqui
                });
            }
        } catch (IOException e) {
            System.err.println("Conexão com o servidor perdida.");
            // Lógica para notificar a UI sobre a desconexão
        }
    }
}