package newpix.views;

import newpix.Cliente;
import newpix.controllers.JsonController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationFrame extends JFrame {
    private static final Color ROXO_PRINCIPAL = new Color(95, 49, 230);
    private static final Color CINZA_FUNDO_PAINEL = new Color(245, 245, 245);
    private static final Color CINZA_BORDA = new Color(220, 220, 220);
    private static final Font CAMPO_FONT = new Font("SansSerif", Font.PLAIN, 16);
    private static final Font BOTAO_PRINCIPAL_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 14);

    private final JTextField ipField, portaField, nomeField;
    private JFormattedTextField cpfField;
    private final JPasswordField senhaField, confirmaSenhaField;
    private final JLabel nomeLabel, confirmaSenhaLabel, senhaLabel;
    private final JButton mainActionButton;
    private final JButton switchModeButton;
    private final JLabel titleLabel;

    private boolean isRegistrationMode = false;

    public AuthenticationFrame() {
        setTitle("NewPix");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CINZA_FUNDO_PAINEL);

        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setBackground(CINZA_FUNDO_PAINEL);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbcWrapper = new GridBagConstraints();
        gbcWrapper.insets = new Insets(20,20,20,20);
        formWrapper.add(formPanel, gbcWrapper);
        mainPanel.add(formWrapper, BorderLayout.CENTER);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        titleLabel = new JLabel("Bem-vindo ao NewPix");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 5, 20, 5);
        formPanel.add(titleLabel, gbc);
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridwidth = 1;

        gbc.gridy = 1; formPanel.add(createLabel("CPF"), gbc);

        gbc.gridy = 2;
        try {
            MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
            cpfFormatter.setPlaceholderCharacter(' ');
            cpfField = new JFormattedTextField(cpfFormatter);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            cpfField = new JFormattedTextField();
        }
        styleTextField(cpfField);
        MainAppFrame.PlaceholderUtil.addPlaceholder(cpfField, "000.000.000-00");
        formPanel.add(cpfField, gbc);

        gbc.gridy = 3; nomeLabel = createLabel("Nome Completo"); formPanel.add(nomeLabel, gbc);
        gbc.gridy = 4; nomeField = new JTextField(); styleTextField(nomeField); formPanel.add(nomeField, gbc);
        MainAppFrame.PlaceholderUtil.addPlaceholder(nomeField, "Seu nome completo");

        gbc.gridy = 5; senhaLabel = createLabel("Senha"); formPanel.add(senhaLabel, gbc);
        gbc.gridy = 6; senhaField = new JPasswordField(); styleTextField(senhaField); formPanel.add(senhaField, gbc);
        MainAppFrame.PlaceholderUtil.addPlaceholder(senhaField, "••••••");

        gbc.gridy = 7; confirmaSenhaLabel = createLabel("Confirmar Senha"); formPanel.add(confirmaSenhaLabel, gbc);
        gbc.gridy = 8; confirmaSenhaField = new JPasswordField(); styleTextField(confirmaSenhaField); formPanel.add(confirmaSenhaField, gbc);
        MainAppFrame.PlaceholderUtil.addPlaceholder(confirmaSenhaField, "••••••");

        gbc.gridy = 9;
        gbc.insets = new Insets(20, 5, 8, 5);
        mainActionButton = createPrimaryButton("Login");
        formPanel.add(mainActionButton, gbc);

        gbc.gridy = 10;
        gbc.insets = new Insets(0, 5, 8, 5);
        switchModeButton = new JButton("Não tem uma conta? Cadastre-se");
        switchModeButton.putClientProperty("JButton.buttonType", "borderless");
        switchModeButton.setForeground(ROXO_PRINCIPAL);
        switchModeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(switchModeButton, gbc);

        JPanel connectionPanel = new JPanel(new GridBagLayout());
        connectionPanel.setBorder(BorderFactory.createTitledBorder(
            new EmptyBorder(10, 10, 10, 10),
            "Endereço do servidor",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            LABEL_FONT,
            Color.DARK_GRAY
        ));
        connectionPanel.setBackground(CINZA_FUNDO_PAINEL);

        GridBagConstraints gbcConn = new GridBagConstraints();
        gbcConn.insets = new Insets(5, 5, 5, 5);
        gbcConn.fill = GridBagConstraints.HORIZONTAL;
        gbcConn.weightx = 1.0;

        gbcConn.gridx = 0; gbcConn.gridy = 0; gbcConn.weightx = 0; connectionPanel.add(new JLabel("IP:"), gbcConn);
        gbcConn.gridx = 1; gbcConn.gridy = 0; gbcConn.weightx = 1; ipField = new JTextField("127.0.0.1", 15); styleTextField(ipField); connectionPanel.add(ipField, gbcConn);

        gbcConn.gridx = 0; gbcConn.gridy = 1; gbcConn.weightx = 0; connectionPanel.add(new JLabel("Porta:"), gbcConn);
        gbcConn.gridx = 1; gbcConn.gridy = 1; gbcConn.weightx = 1; portaField = new JTextField("20000", 15); styleTextField(portaField); connectionPanel.add(portaField, gbcConn);

        mainPanel.add(connectionPanel, BorderLayout.SOUTH);

        mainActionButton.addActionListener(e -> performMainAction());
        switchModeButton.addActionListener(e -> toggleMode());

        toggleMode();
        toggleMode();

        add(mainPanel);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    private void toggleMode() {
        isRegistrationMode = !isRegistrationMode;
        if (isRegistrationMode) {
            titleLabel.setText("Crie sua conta");
            nomeLabel.setVisible(true);
            nomeField.setVisible(true);
            senhaLabel.setText("Senha (mín. 6 caracteres)");
            confirmaSenhaLabel.setVisible(true);
            confirmaSenhaField.setVisible(true);
            mainActionButton.setText("Confirmar Cadastro");
            switchModeButton.setText("Já tem uma conta? Entrar");
        } else {
            titleLabel.setText("Bem-vindo ao NewPix");
            nomeLabel.setVisible(false);
            nomeField.setVisible(false);
            senhaLabel.setText("Senha");
            confirmaSenhaLabel.setVisible(false);
            confirmaSenhaField.setVisible(false);
            mainActionButton.setText("Login");
            switchModeButton.setText("Não tem uma conta? Cadastre-se");
        }
        pack();
        setLocationRelativeTo(null);
    }

    private void performMainAction() {
        if (isRegistrationMode) {
            String senha = new String(senhaField.getPassword());
            String confirmaSenha = new String(confirmaSenhaField.getPassword());

            if (senha.length() < 6) {
                JOptionPane.showMessageDialog(this, "A senha deve ter no mínimo 6 caracteres.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
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
            mainActionButton.setText("Conectando...");

            // Etapa 1: Enviar apenas a requisição de conexão
            Map<String, String> connectRequest = new HashMap<>();
            connectRequest.put("operacao", "conectar");
            cliente.sendMessage(JsonController.toJson(connectRequest));

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
        
        // --- CORREÇÃO ADICIONADA ---
        // Verifica se a resposta do servidor é um JSON inválido (lixo) ou nulo
        if (response == null) {
            JOptionPane.showMessageDialog(this, "O servidor enviou uma resposta inválida ou corrompida.", "Erro de Protocolo", JOptionPane.ERROR_MESSAGE);
            resetButton();
            return;
        }
        // --- FIM DA CORREÇÃO ---

        boolean status = (boolean) response.getOrDefault("status", false);
        String info = (String) response.get("info");
        String operacao = (String) response.get("operacao");

        if ("conectar".equals(operacao)) {
            if (status) {
                // Etapa 2: Conexão bem-sucedida, agora enviar login/cadastro
                mainActionButton.setText("Autenticando...");
                Map<String, String> request = new HashMap<>();
                if (isRegistrationMode) {
                    request.put("operacao", "usuario_criar");
                    request.put("nome", nomeField.getText());
                    request.put("cpf", cpfField.getText());
                    request.put("senha", new String(senhaField.getPassword()));
                } else {
                    request.put("operacao", "usuario_login");
                    request.put("cpf", cpfField.getText());
                    request.put("senha", new String(senhaField.getPassword()));
                }
                Cliente.getInstance().sendMessage(JsonController.toJson(request));
            } else {
                JOptionPane.showMessageDialog(this, info, "Falha na Conexão", JOptionPane.ERROR_MESSAGE);
                resetButton();
            }
            return; 
        }

        if (status) {
            if ("usuario_login".equals(operacao)) {
                Cliente.getInstance().setToken((String) response.get("token"));
                MainAppFrame mainApp = new MainAppFrame();
                mainApp.setVisible(true);
                this.dispose();
                return;
            }

            if("usuario_criar".equals(operacao)) {
                JOptionPane.showMessageDialog(this, info, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                toggleMode();
                cpfField.setValue(null);
                nomeField.setText("");
                senhaField.setText("");
                confirmaSenhaField.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(this, info, "Falha na Operação", JOptionPane.ERROR_MESSAGE);
        }
        
        resetButton();
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        return label;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BOTAO_PRINCIPAL_FONT);
        button.setBackground(ROXO_PRINCIPAL);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(100, 45));
        return button;
    }

    private void styleTextField(JComponent field) {
        field.setFont(CAMPO_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 1, 1, 1, CINZA_BORDA),
            new EmptyBorder(10, 10, 10, 10)
        ));
    }
}