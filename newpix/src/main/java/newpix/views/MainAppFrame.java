package newpix.views;

import newpix.Cliente;
import newpix.controllers.JsonController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
    private JPasswordField novaSenhaField;

    private JSpinner dataInicialSpinner;
    private JSpinner dataFinalSpinner;


    public MainAppFrame() {
        setTitle("NewPix - Sua Conta");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                performLogout();
            }
        });

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

        // --- PAINEL SUPERIOR ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        
        // --- NOVO: Container para Saldo e Botão de Atualizar ---
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        balancePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        balanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
        balancePanel.add(balanceLabel);

        // Adiciona um pequeno espaço
        balancePanel.add(Box.createRigidArea(new Dimension(10, 0)));

        // Cria o botão de atualizar
        JButton refreshButton = new JButton("Atualizar Saldo");
        refreshButton.addActionListener(e -> loadUserData());
        balancePanel.add(refreshButton);
        
        topPanel.add(welcomeLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(balancePanel); // Adiciona o painel com saldo e botão
        panel.add(topPanel, BorderLayout.NORTH);

        // --- PAINEL CENTRAL ---
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

        // --- PAINEL INFERIOR ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        bottomPanel.add(logoutButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // --- AÇÕES DOS BOTÕES ---
        pixButton.addActionListener(e -> cardLayout.show(mainPanel, "PIX"));
        extratoButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "EXTRATO");
            buscarExtrato();
        });
        depositoButton.addActionListener(e -> cardLayout.show(mainPanel, "DEPOSITO"));
        meusDadosButton.addActionListener(e -> cardLayout.show(mainPanel, "MEUS_DADOS"));
        logoutButton.addActionListener(e -> performLogout());
        
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

        Date hoje = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(hoje);
        cal.add(Calendar.DAY_OF_MONTH, -31);
        Date dataInicialDefault = cal.getTime();

        dataInicialSpinner = new JSpinner(new SpinnerDateModel(dataInicialDefault, null, hoje, Calendar.DAY_OF_MONTH));
        dataFinalSpinner = new JSpinner(new SpinnerDateModel(hoje, null, hoje, Calendar.DAY_OF_MONTH));

        dataInicialSpinner.setEditor(new JSpinner.DateEditor(dataInicialSpinner, "dd/MM/yyyy"));
        dataFinalSpinner.setEditor(new JSpinner.DateEditor(dataFinalSpinner, "dd/MM/yyyy"));
        
        controlsPanel.add(dataInicialSpinner);
        controlsPanel.add(new JLabel("Até:"));
        controlsPanel.add(dataFinalSpinner);

        JButton buscarButton = new JButton("Buscar");
        controlsPanel.add(buscarButton);
        topContainer.add(controlsPanel, BorderLayout.CENTER);
        
        panel.add(topContainer, BorderLayout.NORTH);

        String[] colunas = {"Data/Hora", "Tipo", "Participante", "Valor (R$)"};
        extratoTableModel = new DefaultTableModel(colunas, 0);
        JTable extratoTable = new JTable(extratoTableModel);
        panel.add(new JScrollPane(extratoTable), BorderLayout.CENTER);
        
        buscarButton.addActionListener(e -> buscarExtrato());

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

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Nova Senha (mín. 8):"), gbc);
        gbc.gridx = 1; novaSenhaField = new JPasswordField(15); formPanel.add(novaSenhaField, gbc);
        
        gbc.gridy = 2; gbc.gridwidth = 2;
        JButton salvarButton = new JButton("Salvar Alterações");
        salvarButton.addActionListener(e -> atualizarDados());
        formPanel.add(salvarButton, gbc);
        
        gbc.gridy = 3; gbc.insets = new Insets(20, 5, 20, 5); formPanel.add(new JSeparator(), gbc);
        
        gbc.gridy = 4; gbc.insets = new Insets(5, 5, 5, 5);
        JButton deletarButton = new JButton("Deletar Minha Conta");
        deletarButton.setBackground(new Color(220, 53, 69));
        deletarButton.setForeground(Color.WHITE);
        deletarButton.addActionListener(e -> deletarConta());
        formPanel.add(deletarButton, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    public void loadUserData() {
        Cliente cliente = Cliente.getInstance();
        if (cliente.getToken() == null) {
            JOptionPane.showMessageDialog(this, "Erro: Token de sessão não encontrado. Faça o login novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            performLogout();
            return;
        }

        Map<String, String> request = new HashMap<>();
        request.put("operacao", "usuario_ler");
        request.put("token", cliente.getToken());
        cliente.sendMessage(JsonController.toJson(request));
    }

    private void performLogout() {
        Cliente.getInstance().logout();
        this.dispose();
        System.exit(0);
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

    private void buscarExtrato() {
        Date dataInicialDate = (Date) dataInicialSpinner.getValue();
        Date dataFinalDate = (Date) dataFinalSpinner.getValue();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        String dataInicialStr = sdf.format(dataInicialDate) + "T00:00:00Z";
        String dataFinalStr = sdf.format(dataFinalDate) + "T23:59:59Z";

        Map<String, String> request = new HashMap<>();
        request.put("operacao", "transacao_ler");
        request.put("token", Cliente.getInstance().getToken());
        request.put("data_inicial", dataInicialStr);
        request.put("data_final", dataFinalStr);
        Cliente.getInstance().sendMessage(JsonController.toJson(request));
    }

    private void atualizarDados() {
        String novoNome = novoNomeField.getText().trim();
        String novaSenha = new String(novaSenhaField.getPassword());
        
        if (novoNome.isEmpty() && novaSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha pelo menos um campo (novo nome ou nova senha) para atualizar.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, Object> request = new HashMap<>();
        request.put("operacao", "usuario_atualizar");
        request.put("token", Cliente.getInstance().getToken());
        
        Map<String, String> userData = new HashMap<>();
        if (!novoNome.isEmpty()) {
            userData.put("nome", novoNome);
        }
        if (novaSenha.length() >= 8) {
            userData.put("senha", novaSenha);
        } else if (novaSenha.length() > 0) {
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
                case "usuario_logout":
                    this.dispose();
                    new AuthenticationFrame().setVisible(true);
                    break;
                case "notificacao_pix_recebido":
                    handlePixRecebido(response);
                    break;
            }
        });
    }
    
    private void handlePixRecebido(Map<String, Object> response) {
        String info = (String) response.get("info");
        double novoSaldo = ((Number) response.get("novo_saldo")).doubleValue();
        
        JOptionPane.showMessageDialog(this, info, "PIX Recebido!", JOptionPane.INFORMATION_MESSAGE);
        
        balanceLabel.setText(String.format("Saldo: R$ %.2f", novoSaldo));
    }
    
    private void handleUsuarioLerResponse(Map<String, Object> response) {
        Boolean status = (Boolean) response.get("status");
        if (status != null && status) {
            @SuppressWarnings("unchecked")
            Map<String, Object> usuario = (Map<String, Object>) response.get("usuario");
            welcomeLabel.setText("Bem-vindo(a), " + usuario.get("nome") + "!");
            balanceLabel.setText(String.format("Saldo: R$ %.2f", (Double) usuario.get("saldo")));
            
            // --- NOVO: Feedback visual para o usuário ---
            JOptionPane.showMessageDialog(this, "Saldo atualizado com sucesso!", "Informação", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + response.get("info"), "Erro", JOptionPane.ERROR_MESSAGE);
            performLogout();
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
            performLogout();
        }
    }

    @SuppressWarnings("unchecked")
    private void handleTransacaoLerResponse(Map<String, Object> response) {
        extratoTableModel.setRowCount(0);
        Boolean status = (Boolean) response.getOrDefault("status", false);
        if (status) {
            List<Map<String, Object>> transacoes = (List<Map<String, Object>>) response.get("transacoes");
            
            for (Map<String, Object> t : transacoes) {
                String tipo = (String) t.get("tipo");
                double valor = (Double) t.get("valor");
                String valorFormatado = String.format("R$ %.2f", valor);
                
                extratoTableModel.addRow(new Object[]{
                    t.get("data"),
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