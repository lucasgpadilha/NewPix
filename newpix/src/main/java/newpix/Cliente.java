package newpix;

import newpix.controllers.JsonController;
import newpix.views.AuthenticationFrame;
import newpix.views.MainAppFrame;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
            System.out.println("[CLIENTE - ENVIADO]: " + msg);
            out.println(msg);
        } else {
            JOptionPane.showMessageDialog(null, "Não conectado. Não é possível enviar a mensagem.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void logout() {
        if (token != null && isConnected()) {
            Map<String, String> request = new HashMap<>();
            request.put("operacao", "usuario_logout");
            request.put("token", token);
            sendMessage(JsonController.toJson(request));
        }
        closeConnection();
    }

    private void listenToServer() {
        try {
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                System.out.println("[CLIENTE - RECEBIDO]: " + fromServer);
                final String serverResponse = fromServer;
                SwingUtilities.invokeLater(() -> {
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
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Erro ao fechar a conexão do cliente: " + e.getMessage());
        } finally {
            socket = null;
            token = null;
        }
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}