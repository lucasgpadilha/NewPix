package newpix;

import newpix.views.AuthenticationFrame;
import newpix.views.MainAppFrame;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    // --- Singleton ---
    private static Cliente instance;
    private Cliente() {}
    public static synchronized Cliente getInstance() {
        if (instance == null) {
            instance = new Cliente();
        }
        return instance;
    }

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // --- Referências das Janelas ---
    private AuthenticationFrame authenticationFrame;
    private MainAppFrame mainAppFrame;
    private String token;

    public void setAuthenticationFrame(AuthenticationFrame frame) { this.authenticationFrame = frame; }
    public void setMainAppFrame(MainAppFrame frame) { this.mainAppFrame = frame; }

    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    public void startConnection(String ip, int port) throws IOException {
        if (isConnected()) return;
        
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        new Thread(this::listenToServer).start();
    }

    public void sendMessage(String msg) {
        if (isConnected()) {
            out.println(msg);
        } else {
            JOptionPane.showMessageDialog(null, "Não conectado. Não é possível enviar a mensagem.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listenToServer() {
        try {
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                final String serverResponse = fromServer;
                SwingUtilities.invokeLater(() -> {
                    // Direciona a resposta para a janela que estiver ativa
                    if (authenticationFrame != null && authenticationFrame.isShowing()) {
                        authenticationFrame.handleServerResponse(serverResponse);
                    } else if (mainAppFrame != null && mainAppFrame.isShowing()) {
                        mainAppFrame.handleServerResponse(serverResponse);
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Conexão com o servidor perdida.");
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(null, "Conexão com o servidor perdida.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE)
            );
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}