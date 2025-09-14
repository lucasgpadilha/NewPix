package newpix.views;

import newpix.Cliente;
import newpix.controllers.JsonController;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationFrame extends JFrame {
    private final JTextField ipField, portaField, nomeField;
    private final JFormattedTextField cpfField; // Alterado para JFormattedTextField
    private final JPasswordField senhaField, confirmaSenhaField;
    private final JLabel nomeLabel, confirmaSenhaLabel;
    private final JButton mainActionButton;
    private final JButton switchModeButton;

    private boolean isRegistrationMode = false;

    public AuthenticationFrame() {
        setTitle("NewPix - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // --- Painel Principal ---
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Campo de CPF com Máscara ---
        JFormattedTextField tempCpfField;
        try {
            MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
            tempCpfField = new JFormattedTextField(cpfFormatter);
        } catch (java.text.ParseException e) {
            // Em caso de erro na máscara, usa um campo de texto normal como fallback
            e.printStackTrace();
            tempCpfField = new JFormattedTextField();
        }
        cpfField = tempCpfField;
        cpfField.setColumns(15);

        // --- Campos de Autenticação ---
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(cpfField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; nomeLabel = new JLabel("Nome Completo:"); panel.add(nomeLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; nomeField = new JTextField(15); panel.add(nomeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Senha (mín. 8 caracteres):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; senhaField = new JPasswordField(15); panel.add(senhaField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; confirmaSenhaLabel = new JLabel("Confirmar Senha:"); panel.add(confirmaSenhaLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; confirmaSenhaField = new JPasswordField(15); panel.add(confirmaSenhaField, gbc);

        // --- Separador ---
        gbc.gridy = 4; gbc.gridwidth = 2; panel.add(new JSeparator(), gbc);

        // --- Campos de Conexão ---
        gbc.gridy = 5; gbc.gridwidth = 1; gbc.gridx = 0; panel.add(new JLabel("IP Servidor:"), gbc);
        gbc.gridx = 1; ipField = new JTextField("127.0.0.1", 15); panel.add(ipField, gbc);

        gbc.gridy = 6; gbc.gridx = 0; panel.add(new JLabel("Porta:"), gbc);
        gbc.gridx = 1; portaField = new JTextField("12345", 15); panel.add(portaField, gbc);

        // --- Botões de Ação ---
        mainActionButton = new JButton("Login");
        switchModeButton = new JButton("Não tem uma conta? Cadastre-se");
        switchModeButton.setBorderPainted(false);
        switchModeButton.setContentAreaFilled(false);
        switchModeButton.setForeground(Color.BLUE);
        switchModeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 2; panel.add(mainActionButton, gbc);
        gbc.gridy = 8; panel.add(switchModeButton, gbc);

        // --- Listeners ---
        mainActionButton.addActionListener(e -> performMainAction());
        switchModeButton.addActionListener(e -> toggleMode());

        // --- Estado Inicial (Modo Login) ---
        nomeLabel.setVisible(false);
        nomeField.setVisible(false);
        confirmaSenhaLabel.setVisible(false);
        confirmaSenhaField.setVisible(false);

        add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    private void toggleMode() {
        isRegistrationMode = !isRegistrationMode;
        if (isRegistrationMode) {
            // Mudar para o modo de Cadastro
            setTitle("NewPix - Cadastro");
            nomeLabel.setVisible(true);
            nomeField.setVisible(true);
            confirmaSenhaLabel.setVisible(true);
            confirmaSenhaField.setVisible(true);
            mainActionButton.setText("Confirmar Cadastro");
            switchModeButton.setText("Já tem uma conta? Entrar");
        } else {
            // Mudar para o modo de Login
            setTitle("NewPix - Login");
            nomeLabel.setVisible(false);
            nomeField.setVisible(false);
            confirmaSenhaLabel.setVisible(false);
            confirmaSenhaField.setVisible(false);
            mainActionButton.setText("Login");
            switchModeButton.setText("Não tem uma conta? Cadastre-se");
        }
        pack(); // Reajusta o tamanho da janela
    }

    private void performMainAction() {
        // Validações client-side antes de tentar a conexão
        if (isRegistrationMode) {
            String senha = new String(senhaField.getPassword());
            String confirmaSenha = new String(confirmaSenhaField.getPassword());

            if (senha.length() < 8) {
                JOptionPane.showMessageDialog(this, "A senha deve ter no mínimo 8 caracteres.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!senha.equals(confirmaSenha)) {
                JOptionPane.showMessageDialog(this, "As senhas não coincidem.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        String ip = ipField.getText();
        String portaStr = portaField.getText();

        if (ip.trim().isEmpty() || portaStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "IP e Porta do servidor são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int porta = Integer.parseInt(portaStr);
            Cliente cliente = Cliente.getInstance();
            cliente.setAuthenticationFrame(this);

            if (!cliente.isConnected()) {
                cliente.startConnection(ip, porta);
            }
            
            mainActionButton.setEnabled(false);
            mainActionButton.setText("Aguardando...");

            if (isRegistrationMode) {
                Map<String, String> request = new HashMap<>();
                request.put("operacao", "usuario_criar");
                request.put("nome", nomeField.getText());
                request.put("cpf", cpfField.getText());
                request.put("senha", new String(senhaField.getPassword()));
                cliente.sendMessage(JsonController.toJson(request));
            } else {
                Map<String, String> request = new HashMap<>();
                request.put("operacao", "usuario_login");
                request.put("cpf", cpfField.getText());
                request.put("senha", new String(senhaField.getPassword()));
                cliente.sendMessage(JsonController.toJson(request));
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro de conexão: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            resetButton();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Porta inválida. Digite apenas números.", "Erro", JOptionPane.ERROR_MESSAGE);
            resetButton();
        }
    }
    
    private void resetButton() {
        mainActionButton.setEnabled(true);
        mainActionButton.setText(isRegistrationMode ? "Confirmar Cadastro" : "Login");
    }

    public void handleServerResponse(String jsonResponse) {
        Map<String, Object> response = JsonController.fromJson(jsonResponse, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
        boolean status = (boolean) response.get("status");
        String info = (String) response.get("info");

        if (status) {
            JOptionPane.showMessageDialog(this, info, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            if (isRegistrationMode) {
                toggleMode();
                // Limpa os campos após o cadastro bem-sucedido
                cpfField.setValue(null);
                nomeField.setText("");
                senhaField.setText("");
                confirmaSenhaField.setText("");
            } else {
                Cliente.getInstance().setToken((String) response.get("token"));
                MainAppFrame mainApp = new MainAppFrame();
                mainApp.setVisible(true);
                this.dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this, info, "Falha na Operação", JOptionPane.ERROR_MESSAGE);
        }
        
        resetButton();
    }
}