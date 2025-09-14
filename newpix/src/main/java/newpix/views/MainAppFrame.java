package newpix.views;

import newpix.Cliente;
import newpix.controllers.JsonController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainAppFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final JLabel welcomeLabel = new JLabel("Carregando...");
    private final JLabel balanceLabel = new JLabel("Saldo: R$ --,--");
    
    private DefaultTableModel extratoTableModel;
    private JFormattedTextField pixCpfField;
    private JTextField pixValorField;
    private JTextField depositoValorField;
    private JTextField novoNomeField;
    private JPasswordField senhaAtualField, novaSenhaField;

    public MainAppFrame() {
        setTitle("NewPix - Sua Conta");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel dashboardPanel = createDashboardPanel();
        JPanel pixPanel = createPixPanel();
        JPanel extratoPanel = createExtratoPanel();
        JPanel depositoPanel = createDepositoPanel();
        JPanel meusDadosPanel = createMeusDadosPanel();

        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(pixPanel, "PIX");
        mainPanel.add(extratoPanel, "EXTRATO");
        mainPanel.add(depositoPanel, "DEPOSITO");
        mainPanel.add(meusDadosPanel, "MEUS_DADOS");

        add(mainPanel);

        Cliente.getInstance().setMainAppFrame(this);
        loadUserData();
    }

    private JPanel createHeader(String title) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("Voltar");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "DASHBOARD"));
        headerPanel.add(backButton, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        balanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
        topPanel.add(welcomeLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(balanceLabel);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        centerPanel.setBorder(new EmptyBorder(40, 20, 40, 20));
        JButton pixButton = new JButton("Fazer um Pix");
        JButton extratoButton = new JButton("Meu Extrato");
        JButton depositoButton = new JButton("Depositar");
        JButton meusDadosButton = new JButton("Meus Dados");

        Font buttonFont = new Font("SansSerif", Font.BOLD, 18);
        pixButton.setFont(buttonFont);
        extratoButton.setFont(buttonFont);
        depositoButton.setFont(buttonFont);
        meusDadosButton.setFont(buttonFont);
        
        centerPanel.add(pixButton);
        centerPanel.add(extratoButton);
        centerPanel.add(depositoButton);
        centerPanel.add(meusDadosButton);
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        bottomPanel.add(logoutButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        pixButton.addActionListener(e -> cardLayout.show(mainPanel, "PIX"));
        extratoButton.addActionListener(e -> cardLayout.show(mainPanel, "EXTRATO"));
        depositoButton.addActionListener(e -> cardLayout.show(mainPanel, "DEPOSITO"));
        meusDadosButton.addActionListener(e -> cardLayout.show(mainPanel, "MEUS_DADOS"));
        logoutButton.addActionListener(e -> logout());
        
        return panel;
    }
    
    private JPanel createPixPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 100, 20, 100));
        panel.add(createHeader("Fazer um Pix"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("CPF do Destinatário:"), gbc);
        try {
            MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
            pixCpfField = new JFormattedTextField(cpfFormatter);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            pixCpfField = new JFormattedTextField();
        }
        pixCpfField.setColumns(15);
        gbc.gridx = 1; formPanel.add(pixCpfField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Valor (R$):"), gbc);
        gbc.gridx = 1; pixValorField = new JTextField(15); formPanel.add(pixValorField, gbc);

        gbc.gridy = 2; gbc.gridwidth = 2; 
        JButton transferButton = new JButton("Transferir");
        transferButton.addActionListener(e -> realizarPix());
        formPanel.add(transferButton, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createExtratoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(createHeader("Meu Extrato"), BorderLayout.NORTH);

        JPanel controlsPanel = new JPanel(new FlowLayout());
        controlsPanel.add(new JLabel("De:"));
        JTextField dataInicialField = new JTextField("2025-01-01T00:00:00Z", 20);
        controlsPanel.add(dataInicialField);
        controlsPanel.add(new JLabel("Até:"));
        JTextField dataFinalField = new JTextField(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date()), 20);
        controlsPanel.add(dataFinalField);
        JButton buscarButton = new JButton("Buscar");
        controlsPanel.add(buscarButton);
        topContainer.add(controlsPanel, BorderLayout.CENTER);
        
        panel.add(topContainer, BorderLayout.NORTH);

        String[] colunas = {"Data/Hora", "Tipo", "Participante", "Valor (R$)"};
        extratoTableModel = new DefaultTableModel(colunas, 0);
        JTable extratoTable = new JTable(extratoTableModel);
        panel.add(new JScrollPane(extratoTable), BorderLayout.CENTER);
        
        buscarButton.addActionListener(e -> buscarExtrato(dataInicialField.getText(), dataFinalField.getText()));

        return panel;
    }
    
    private JPanel createDepositoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 100, 20, 100));
        panel.add(createHeader("Depositar"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Valor do Depósito (R$):"), gbc);
        gbc.gridx = 1; depositoValorField = new JTextField(15); formPanel.add(depositoValorField, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 2;
        JButton depositarButton = new JButton("Confirmar Depósito");
        depositarButton.addActionListener(e -> realizarDeposito());
        formPanel.add(depositarButton, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createMeusDadosPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 100, 20, 100));
        panel.add(createHeader("Meus Dados"), BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Novo Nome:"), gbc);
        gbc.gridx = 1; novoNomeField = new JTextField(15); formPanel.add(novoNomeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Senha Atual (obrigatória):"), gbc);
        gbc.gridx = 1; senhaAtualField = new JPasswordField(15); formPanel.add(senhaAtualField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Nova Senha (mín. 8):"), gbc);
        gbc.gridx = 1; novaSenhaField = new JPasswordField(15); formPanel.add(novaSenhaField, gbc);
        
        gbc.gridy = 3; gbc.gridwidth = 2;
        JButton salvarButton = new JButton("Salvar Alterações");
        salvarButton.addActionListener(e -> atualizarDados());
        formPanel.add(salvarButton, gbc);
        
        gbc.gridy = 4; gbc.insets = new Insets(20, 5, 20, 5); formPanel.add(new JSeparator(), gbc);
        
        gbc.gridy = 5; gbc.insets = new Insets(5, 5, 5, 5);
        JButton deletarButton = new JButton("Deletar Minha Conta");
        deletarButton.setBackground(new Color(220, 53, 69));
        deletarButton.setForeground(Color.WHITE);
        deletarButton.addActionListener(e -> deletarConta());
        formPanel.add(deletarButton, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private void loadUserData() {
        Cliente cliente = Cliente.getInstance();
        if (cliente.getToken() == null) {
            JOptionPane.showMessageDialog(this, "Erro: Token de sessão não encontrado. Faça o login novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            logout();
            return;
        }

        Map<String, String> request = new HashMap<>();
        request.put("operacao", "usuario_ler");
        request.put("token", cliente.getToken());
        cliente.sendMessage(JsonController.toJson(request));
    }

    private void logout() {
        this.dispose();
        new AuthenticationFrame().setVisible(true);
    }
    
    private void realizarDeposito() {
        try {
            double valor = Double.parseDouble(depositoValorField.getText().replace(",", "."));
            Map<String, Object> request = new HashMap<>();
            request.put("operacao", "depositar");
            request.put("token", Cliente.getInstance().getToken());
            request.put("valor_enviado", valor);
            Cliente.getInstance().sendMessage(JsonController.toJson(request));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um valor numérico válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void realizarPix() {
        try {
            String cpfDestino = pixCpfField.getText();
            double valor = Double.parseDouble(pixValorField.getText().replace(",", "."));
            
            Map<String, Object> request = new HashMap<>();
            request.put("operacao", "transacao_criar");
            request.put("token", Cliente.getInstance().getToken());
            request.put("cpf_destino", cpfDestino);
            request.put("valor", valor);
            Cliente.getInstance().sendMessage(JsonController.toJson(request));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um valor numérico válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarExtrato(String dataInicial, String dataFinal) {
        Map<String, String> request = new HashMap<>();
        request.put("operacao", "transacao_ler");
        request.put("token", Cliente.getInstance().getToken());
        request.put("data_inicial", dataInicial);
        request.put("data_final", dataFinal);
        Cliente.getInstance().sendMessage(JsonController.toJson(request));
    }

    private void atualizarDados() {
        String senhaAtual = new String(senhaAtualField.getPassword());
        if (senhaAtual.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A senha atual é obrigatória para qualquer alteração.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, Object> request = new HashMap<>();
        request.put("operacao", "usuario_atualizar");
        request.put("token", Cliente.getInstance().getToken());
        
        Map<String, String> userData = new HashMap<>();
        userData.put("senha_atual", senhaAtual);
        if (!novoNomeField.getText().trim().isEmpty()) {
            userData.put("nome", novoNomeField.getText().trim());
        }
        if (new String(novaSenhaField.getPassword()).length() >= 8) {
            userData.put("senha", new String(novaSenhaField.getPassword()));
        } else if (new String(novaSenhaField.getPassword()).length() > 0) {
            JOptionPane.showMessageDialog(this, "A nova senha deve ter no mínimo 8 caracteres.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        request.put("usuario", userData);
        Cliente.getInstance().sendMessage(JsonController.toJson(request));
    }

    private void deletarConta() {
        String senha = JOptionPane.showInputDialog(this, 
            "<html><body width='300'><h2>Confirmar Exclusão</h2>" +
            "<p>Esta ação é irreversível e todos os seus dados serão perdidos.</p>" +
            "<p>Para confirmar a exclusão, digite sua senha:</p></body></html>",
            "Confirmar Exclusão", 
            JOptionPane.WARNING_MESSAGE);
            
        if (senha != null && !senha.isEmpty()) {
            Map<String, String> request = new HashMap<>();
            request.put("operacao", "usuario_deletar");
            request.put("token", Cliente.getInstance().getToken());
            request.put("senha", senha);
            Cliente.getInstance().sendMessage(JsonController.toJson(request));
        }
    }

    public void handleServerResponse(String jsonResponse) {
        Map<String, Object> response = JsonController.fromJson(jsonResponse, new com.fasterxml.jackson.core.type.TypeReference<>() {});
        if (response == null || response.get("operacao") == null) {
            System.err.println("Resposta inválida do servidor: " + jsonResponse);
            return;
        }
        
        String operacao = (String) response.get("operacao");
        
        SwingUtilities.invokeLater(() -> {
            switch(operacao) {
                case "usuario_ler":
                    handleUsuarioLerResponse(response);
                    break;
                case "depositar":
                    handleDepositoResponse(response);
                    break;
                case "transacao_criar":
                    handleTransacaoCriarResponse(response);
                    break;
                case "usuario_atualizar":
                    handleUsuarioAtualizarResponse(response);
                    break;
                case "usuario_deletar":
                    handleUsuarioDeletarResponse(response);
                    break;
                case "transacao_ler":
                    handleTransacaoLerResponse(response);
                    break;
            }
        });
    }
    
    private void handleUsuarioLerResponse(Map<String, Object> response) {
        Boolean status = (Boolean) response.get("status");
        if (status != null && status) {
            @SuppressWarnings("unchecked")
            Map<String, Object> usuario = (Map<String, Object>) response.get("usuario");
            welcomeLabel.setText("Bem-vindo(a), " + usuario.get("nome") + "!");
            balanceLabel.setText(String.format("Saldo: R$ %.2f", (Double) usuario.get("saldo")));
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + response.get("info"), "Erro", JOptionPane.ERROR_MESSAGE);
            logout();
        }
    }
    
    private void handleDepositoResponse(Map<String, Object> response) {
        Boolean status = (Boolean) response.getOrDefault("status", false);
        JOptionPane.showMessageDialog(this, response.get("info"), status ? "Sucesso" : "Erro", 
            status ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (status) {
            depositoValorField.setText("");
            cardLayout.show(mainPanel, "DASHBOARD");
            loadUserData();
        }
    }
    
    private void handleTransacaoCriarResponse(Map<String, Object> response) {
        Boolean status = (Boolean) response.getOrDefault("status", false);
        JOptionPane.showMessageDialog(this, response.get("info"), status ? "Sucesso" : "Erro", 
            status ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (status) {
            pixCpfField.setValue(null);
            pixValorField.setText("");
            cardLayout.show(mainPanel, "DASHBOARD");
            loadUserData();
        }
    }

    private void handleUsuarioAtualizarResponse(Map<String, Object> response) {
        Boolean status = (Boolean) response.getOrDefault("status", false);
        JOptionPane.showMessageDialog(this, response.get("info"), status ? "Sucesso" : "Erro", 
            status ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (status) {
            novoNomeField.setText("");
            senhaAtualField.setText("");
            novaSenhaField.setText("");
            cardLayout.show(mainPanel, "DASHBOARD");
            loadUserData();
        }
    }
    
    private void handleUsuarioDeletarResponse(Map<String, Object> response) {
        Boolean status = (Boolean) response.getOrDefault("status", false);
        JOptionPane.showMessageDialog(this, response.get("info"), status ? "Sucesso" : "Erro", 
            status ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (status) {
            logout();
        }
    }

    @SuppressWarnings("unchecked")
    private void handleTransacaoLerResponse(Map<String, Object> response) {
        extratoTableModel.setRowCount(0);
        Boolean status = (Boolean) response.getOrDefault("status", false);
        if (status) {
            List<Map<String, Object>> transacoes = (List<Map<String, Object>>) response.get("transacoes");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            for (Map<String, Object> t : transacoes) {
                String tipo = (String) t.get("tipo");
                double valor = (Double) t.get("valor");
                String valorFormatado = String.format("R$ %.2f", valor);
                
                extratoTableModel.addRow(new Object[]{
                    sdf.format(new Date((Long)t.get("data"))),
                    tipo.substring(0, 1).toUpperCase() + tipo.substring(1),
                    t.get("outro_participante"),
                    valorFormatado
                });
            }
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao buscar extrato: " + response.get("info"), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}