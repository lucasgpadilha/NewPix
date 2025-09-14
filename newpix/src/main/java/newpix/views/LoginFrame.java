package newpix.views;

import newpix.Cliente;
import newpix.controllers.JsonController;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginFrame extends JFrame {
    private final JTextField cpfField;
    private final JPasswordField senhaField;
    private final JButton btnEntrar;

    public LoginFrame(JFrame parent) {
        setTitle("NewPix - Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("CPF (000.000.000-00):"));
        cpfField = new JTextField();
        panel.add(cpfField);

        panel.add(new JLabel("Senha:"));
        senhaField = new JPasswordField();
        panel.add(senhaField);

        btnEntrar = new JButton("Entrar");
        JButton btnVoltar = new JButton("Voltar");

        panel.add(btnEntrar);
        panel.add(btnVoltar);

        add(panel, BorderLayout.CENTER);

        // Ações dos botões
        btnEntrar.addActionListener(e -> attemptLogin());

        btnVoltar.addActionListener(e -> {
            dispose();
            parent.setVisible(true);
        });
    }

    private void attemptLogin() {
        String cpf = cpfField.getText();
        String senha = new String(senhaField.getPassword());
        
        try {
            Cliente cliente = Cliente.getInstance();
            cliente.setLoginFrame(this); // Informa ao cliente qual tela está ativa

            if (!cliente.isConnected()) {
                String ip = "127.0.0.1"; // Pode ser solicitado via JOptionPane
                int port = 12345;     // Pode ser solicitado via JOptionPane
                cliente.startConnection(ip, port);
            }

            // Criar e enviar a requisição de login
            Map<String, String> request = new HashMap<>();
            request.put("operacao", "usuario_login");
            request.put("cpf", cpf);
            request.put("senha", senha);
            cliente.sendMessage(JsonController.toJson(request));
            
            btnEntrar.setEnabled(false); // Desabilita o botão para evitar cliques duplos
            btnEntrar.setText("Aguardando...");

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro de conexão: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Este método será chamado pelo Cliente quando uma resposta chegar
    public void handleServerResponse(String jsonResponse) {
        Map<String, Object> response = JsonController.fromJson(jsonResponse, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        boolean status = (boolean) response.get("status");
        String info = (String) response.get("info");

        if (status) {
            JOptionPane.showMessageDialog(this, info, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            // Abrir a tela principal da aplicação
            MainAppFrame mainApp = new MainAppFrame(); // Passar o token e dados do usuário
            mainApp.setVisible(true);
            this.dispose(); // Fecha a tela de login
            // parent.dispose(); // Fecha a tela Home
        } else {
            JOptionPane.showMessageDialog(this, info, "Falha no Login", JOptionPane.ERROR_MESSAGE);
            btnEntrar.setEnabled(true);
            btnEntrar.setText("Entrar");
        }
    }
}