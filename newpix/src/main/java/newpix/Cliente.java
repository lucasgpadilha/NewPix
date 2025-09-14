package newpix;

import newpix.views.CadastroFrame;
import newpix.views.LoginFrame;
import newpix.views.MainAppFrame;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    // --- Singleton Implementation ---
    private static Cliente instance;

    private Cliente() {}

    public static synchronized Cliente getInstance() {
        if (instance == null) {
            instance = new Cliente();
        }
        return instance;
    }
    // --- End Singleton ---

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // References to active frames
    private LoginFrame loginFrame;
    private CadastroFrame cadastroFrame;
    private MainAppFrame mainAppFrame;
    private String token;

    // Methods for frames to register themselves
    public void setLoginFrame(LoginFrame frame) { this.loginFrame = frame; }
    public void setCadastroFrame(CadastroFrame frame) { this.cadastroFrame = frame; }
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
            System.err.println("Não conectado. Não é possível enviar a mensagem.");
        }
    }

    private void listenToServer() {
        try {
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                final String serverResponse = fromServer;
                // Use SwingUtilities to safely update the GUI from this thread
                SwingUtilities.invokeLater(() -> {
                    // Route the response to the currently active frame
                    if (loginFrame != null && loginFrame.isShowing()) {
                        loginFrame.handleServerResponse(serverResponse);
                    } else if (cadastroFrame != null && cadastroFrame.isShowing()) {
                        cadastroFrame.handleServerResponse(serverResponse);
                    } else if (mainAppFrame != null && mainAppFrame.isShowing()) {
                        mainAppFrame.handleServerResponse(serverResponse);
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Conexão com o servidor perdida.");
            // Optionally, show a popup to the user
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