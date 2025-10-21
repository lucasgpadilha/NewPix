package newpix.views;

import newpix.Cliente;
import newpix.controllers.JsonController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MainAppFrame extends JFrame {
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private static final Color ROXO_PRINCIPAL = new Color(95, 49, 230);
    private static final Color CINZA_FUNDO_PAINEL = new Color(245, 245, 245);
    private static final Color CINZA_BORDA = new Color(220, 220, 220);
    private static final Font CAMPO_FONT = new Font("SansSerif", Font.PLAIN, 16);
    private static final Font BOTAO_PRINCIPAL_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 14);


    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final JLabel welcomeLabel = new JLabel("Carregando...");
    private final JLabel balanceLabel = new JLabel("R$ --,--");
    private final JLabel balanceTitleLabel = new JLabel("Saldo em conta");


    private DefaultTableModel extratoTableModel;
    private JFormattedTextField pixCpfField;
    private JFormattedTextField pixValorField;
    private JFormattedTextField depositoValorField;
    private JTextField novoNomeField;
    private JPasswordField novaSenhaField;
    private String nomeAtual = "";
    private String cpfAtual = ""; 

    private JSpinner dataInicialSpinner;
    private JSpinner dataFinalSpinner;


    public MainAppFrame() {
        setTitle("NewPix");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        headerPanel.setBorder(new EmptyBorder(15, 20, 25, 20));
        headerPanel.setBackground(CINZA_FUNDO_PAINEL);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("‹ Voltar");
        backButton.putClientProperty("JButton.buttonType", "borderless");
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "DASHBOARD"));
        headerPanel.add(backButton, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(0,0));
        panel.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ROXO_PRINCIPAL);
        topPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        topPanel.add(welcomeLabel, BorderLayout.NORTH);

        balanceTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        balanceTitleLabel.setForeground(new Color(220, 220, 220));
        
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        balanceLabel.setForeground(Color.WHITE);

        JPanel balanceContainer = new JPanel();
        balanceContainer.setLayout(new BoxLayout(balanceContainer, BoxLayout.Y_AXIS));
        balanceContainer.setOpaque(false);
        balanceContainer.add(balanceTitleLabel);
        balanceContainer.add(balanceLabel);
        topPanel.add(balanceContainer, BorderLayout.CENTER);
        
        JButton logoutButton = new JButton("Sair");
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        logoutButton.setOpaque(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> performLogout());
        topPanel.add(logoutButton, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        actionsPanel.setBorder(new EmptyBorder(30, 20, 30, 20));
        actionsPanel.setBackground(Color.WHITE);

        JButton pixButton = createActionButton("Fazer um PIX", "→");
        JButton extratoButton = createActionButton("Meu Extrato", "☰");
        JButton depositoButton = createActionButton("Depositar", "+");
        JButton meusDadosButton = createActionButton("Meus Dados", "☺");
        
        pixButton.addActionListener(e -> cardLayout.show(mainPanel, "PIX"));
        extratoButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "EXTRATO");
            buscarExtrato();
        });
        depositoButton.addActionListener(e -> cardLayout.show(mainPanel, "DEPOSITO"));
        
        meusDadosButton.addActionListener(e -> {
            novoNomeField.setText(this.nomeAtual);
            novoNomeField.setForeground(UIManager.getColor("TextField.foreground"));
            novaSenhaField.setText("");
            cardLayout.show(mainPanel, "MEUS_DADOS");
        });

        actionsPanel.add(pixButton);
        actionsPanel.add(extratoButton);
        actionsPanel.add(depositoButton);
        actionsPanel.add(meusDadosButton);

        panel.add(actionsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createActionButton(String text, String iconChar) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 100));
        button.setBackground(new Color(245, 245, 245));
        button.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel iconLabel = new JLabel(iconChar);
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        button.add(iconLabel, BorderLayout.CENTER);
        button.add(textLabel, BorderLayout.SOUTH);
        
        return button;
    }

    private JFormattedTextField createValorField() {
        DecimalFormat format = new DecimalFormat("#,##0.00");
        format.setParseBigDecimal(true);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        
        JFormattedTextField textField = new JFormattedTextField(formatter);
        textField.setFont(CAMPO_FONT);
        textField.setHorizontalAlignment(JTextField.RIGHT);
        textField.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 1, 1, 1, CINZA_BORDA),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        PlaceholderUtil.addPlaceholder(textField, "0,00");

        return textField;
    }
    
    private JPanel createFormPanel(String title, Component... components) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CINZA_FUNDO_PAINEL);
        panel.add(createHeader(title), BorderLayout.NORTH);

        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel formWrapper = new JPanel();
        formWrapper.setBackground(CINZA_FUNDO_PAINEL);
        formWrapper.setLayout(new GridBagLayout());
        GridBagConstraints gbcWrapper = new GridBagConstraints();
        gbcWrapper.weightx = 1.0;
        gbcWrapper.fill = GridBagConstraints.HORIZONTAL;
        gbcWrapper.insets = new Insets(0, 100, 0, 100);
        formWrapper.add(formContainer, gbcWrapper);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        for (int i = 0; i < components.length; i++) {
            gbc.gridy = i;
            formContainer.add(components[i], gbc);
        }
        
        panel.add(formWrapper, BorderLayout.CENTER);
        return panel;
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
    
    private JPanel createPixPanel() {
        JLabel cpfLabel = new JLabel("CPF do Destinatário");
        cpfLabel.setFont(LABEL_FONT);
        JLabel valorLabel = new JLabel("Valor a ser enviado (R$)");
        valorLabel.setFont(LABEL_FONT);
        
        try {
            MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
            cpfFormatter.setPlaceholderCharacter(' ');
            pixCpfField = new JFormattedTextField(cpfFormatter);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            pixCpfField = new JFormattedTextField();
        }
        styleTextField(pixCpfField);
        PlaceholderUtil.addPlaceholder(pixCpfField, "000.000.000-00");
        
        pixValorField = createValorField();
        
        JButton transferButton = createPrimaryButton("Confirmar Transferência");
        transferButton.addActionListener(e -> realizarPix());
        
        return createFormPanel("Fazer um Pix", cpfLabel, pixCpfField, valorLabel, pixValorField, Box.createRigidArea(new Dimension(0, 15)), transferButton);
    }

    private JPanel createExtratoPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(CINZA_FUNDO_PAINEL);
        panel.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        panel.add(createHeader("Meu Extrato"), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel controlsPanel = new JPanel(new BorderLayout(10,0));
        controlsPanel.setBackground(Color.WHITE);

        JPanel datePickersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        datePickersPanel.setBackground(Color.WHITE);
        
        JLabel periodoLabel = new JLabel("Período:");
        periodoLabel.setFont(LABEL_FONT);
        datePickersPanel.add(periodoLabel);

        Date hoje = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(hoje);
        cal.add(Calendar.DAY_OF_MONTH, -31);
        Date dataInicialDefault = cal.getTime();

        dataInicialSpinner = new JSpinner(new SpinnerDateModel(dataInicialDefault, null, hoje, Calendar.DAY_OF_MONTH));
        dataFinalSpinner = new JSpinner(new SpinnerDateModel(hoje, null, hoje, Calendar.DAY_OF_MONTH));

        dataInicialSpinner.setEditor(new JSpinner.DateEditor(dataInicialSpinner, "dd/MM/yyyy"));
        dataFinalSpinner.setEditor(new JSpinner.DateEditor(dataFinalSpinner, "dd/MM/yyyy"));
        
        datePickersPanel.add(dataInicialSpinner);
        datePickersPanel.add(new JLabel("a"));
        datePickersPanel.add(dataFinalSpinner);
        
        controlsPanel.add(datePickersPanel, BorderLayout.CENTER);

        JButton buscarButton = createPrimaryButton("Buscar");
        buscarButton.setPreferredSize(new Dimension(120, 35));
        controlsPanel.add(buscarButton, BorderLayout.EAST);
        
        contentPanel.add(controlsPanel, BorderLayout.NORTH);

        String[] colunas = {"", "Data/Hora", "Descrição", "Valor"};
        extratoTableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Boolean.class;
                    case 1: return String.class;
                    case 2: return String.class;
                    case 3: return Double.class;
                    default: return Object.class;
                }
            }
        };
        
        JTable extratoTable = new JTable(extratoTableModel);
        
        extratoTable.setRowHeight(30);
        extratoTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        extratoTable.setGridColor(CINZA_BORDA);
        extratoTable.setShowGrid(true);
        extratoTable.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = extratoTable.getTableHeader();
        header.setBackground(CINZA_FUNDO_PAINEL);
        header.setForeground(Color.DARK_GRAY);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        extratoTable.setDefaultRenderer(Object.class, new ExtratoCellRenderer());
        
        extratoTable.getColumnModel().getColumn(0).setMaxWidth(40);
        extratoTable.getColumnModel().getColumn(0).setMinWidth(40);

        JScrollPane scrollPane = new JScrollPane(extratoTable);
        scrollPane.setBorder(new MatteBorder(1, 1, 1, 1, CINZA_BORDA));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        buscarButton.addActionListener(e -> buscarExtrato());

        return panel;
    }
    
    private JPanel createDepositoPanel() {
        JLabel valorLabel = new JLabel("Valor do Depósito (R$)");
        valorLabel.setFont(LABEL_FONT);
        
        depositoValorField = createValorField();
        
        JButton depositarButton = createPrimaryButton("Confirmar Depósito");
        depositarButton.addActionListener(e -> realizarDeposito());
        
        return createFormPanel("Depositar", valorLabel, depositoValorField, Box.createRigidArea(new Dimension(0, 15)), depositarButton);
    }
    
    private JPanel createMeusDadosPanel() {
        JLabel nomeLabel = new JLabel("Alterar Nome"); 
        nomeLabel.setFont(LABEL_FONT);
        novoNomeField = new JTextField();
        styleTextField(novoNomeField);
        PlaceholderUtil.addPlaceholder(novoNomeField, "Seu nome completo"); 

        JLabel senhaLabel = new JLabel("Nova Senha (mín. 6 caracteres)");
        senhaLabel.setFont(LABEL_FONT);
        novaSenhaField = new JPasswordField();
        styleTextField(novaSenhaField);
        PlaceholderUtil.addPlaceholder(novaSenhaField, "••••••");

        JButton salvarButton = createPrimaryButton("Salvar Alterações");
        salvarButton.addActionListener(e -> atualizarDados());
        
        JSeparator separator = new JSeparator();
        separator.setForeground(CINZA_BORDA);
        
        JButton deletarButton = new JButton("Deletar Minha Conta");
        deletarButton.setBackground(new Color(220, 53, 69));
        deletarButton.setForeground(Color.WHITE);
        deletarButton.setOpaque(true);
        deletarButton.setBorderPainted(false);
        deletarButton.setFont(BOTAO_PRINCIPAL_FONT);
        deletarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deletarButton.setPreferredSize(new Dimension(100, 45));
        deletarButton.addActionListener(e -> deletarConta());

        return createFormPanel("Meus Dados", 
            nomeLabel, novoNomeField, 
            senhaLabel, novaSenhaField, 
            Box.createRigidArea(new Dimension(0, 15)), 
            salvarButton, 
            Box.createRigidArea(new Dimension(0, 25)), 
            separator, 
            Box.createRigidArea(new Dimension(0, 25)), 
            deletarButton
        );
    }
    
    private void loadUserData() {
        loadUserData(false);
    }

    private void loadUserData(boolean showFeedback) {
        Cliente cliente = Cliente.getInstance();
        if (cliente.getToken() == null) {
            JOptionPane.showMessageDialog(this, "Erro: Token de sessão não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            performLogout();
            return;
        }

        Map<String, Object> request = new HashMap<>();
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
        Object valorObj = depositoValorField.getValue();
        if (valorObj == null) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um valor.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double valor = ((Number) valorObj).doubleValue();
        Map<String, Object> request = new HashMap<>();
        request.put("operacao", "depositar");
        request.put("token", Cliente.getInstance().getToken());
        request.put("valor_enviado", valor);
        Cliente.getInstance().sendMessage(JsonController.toJson(request));
    }
    
    private void realizarPix() {
        Object valorObj = pixValorField.getValue();
        if (valorObj == null) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um valor.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String cpfDestino = pixCpfField.getText();
        if(cpfDestino.trim().replace(".", "").replace("-","").isEmpty()){
            JOptionPane.showMessageDialog(this, "Por favor, insira um CPF.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double valor = ((Number) valorObj).doubleValue();
        
        Map<String, Object> request = new HashMap<>();
        request.put("operacao", "transacao_criar");
        request.put("token", Cliente.getInstance().getToken());
        request.put("cpf_destino", cpfDestino);
        request.put("valor", valor);
        Cliente.getInstance().sendMessage(JsonController.toJson(request));
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
        
        boolean nomeMudou = !novoNome.isEmpty() && !novoNome.equals(this.nomeAtual);
        boolean senhaMudou = !novaSenha.isEmpty();

        if (!nomeMudou && !senhaMudou) {
            JOptionPane.showMessageDialog(this, "Nenhuma alteração foi feita.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Map<String, Object> request = new HashMap<>();
        request.put("operacao", "usuario_atualizar");
        request.put("token", Cliente.getInstance().getToken());
        
        Map<String, String> userData = new HashMap<>();
        if (nomeMudou) {
            userData.put("nome", novoNome);
        }
        
        if (senhaMudou && novaSenha.length() >= 6) {
            userData.put("senha", novaSenha);
        } else if (senhaMudou && novaSenha.length() > 0) {
            JOptionPane.showMessageDialog(this, "A nova senha deve ter no mínimo 6 caracteres.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        request.put("usuario", userData);
        Cliente.getInstance().sendMessage(JsonController.toJson(request));
    }

    private void deletarConta() {
        int response = JOptionPane.showConfirmDialog(this, 
            "<html><body width='300'><h2>Confirmar Exclusão</h2>" +
            "<p>Esta ação é irreversível.</p>" +
            "<p><b>Deseja deletar sua conta?</b></p></body></html>",
            "Confirmar Exclusão", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (response == JOptionPane.YES_OPTION) {
            Map<String, String> request = new HashMap<>();
            request.put("operacao", "usuario_deletar");
            request.put("token", Cliente.getInstance().getToken());
            Cliente.getInstance().sendMessage(JsonController.toJson(request));
        }
    }

    public void handleServerResponse(String jsonResponse) {
        Map<String, Object> response = JsonController.fromJson(jsonResponse, new com.fasterxml.jackson.core.type.TypeReference<>() {});
        
        if (response == null || response.get("operacao") == null) {
            System.err.println("Resposta inválida do servidor: " + jsonResponse);
            if (response == null) {
                JOptionPane.showMessageDialog(this, "O servidor enviou uma resposta inválida.", "Erro de Protocolo", JOptionPane.ERROR_MESSAGE);
            }
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
        
        balanceLabel.setText(currencyFormatter.format(novoSaldo));
    }
    
    private void handleUsuarioLerResponse(Map<String, Object> response) {
        Boolean status = (Boolean) response.get("status");
        if (status != null && status) {
            @SuppressWarnings("unchecked")
            Map<String, Object> usuario = (Map<String, Object>) response.get("usuario");
            
            this.nomeAtual = (String) usuario.get("nome");
            this.cpfAtual = (String) usuario.get("cpf"); 
            
            welcomeLabel.setText("Olá, " + this.nomeAtual.split(" ")[0] + "!");
            
            double saldo = (Double) usuario.get("saldo");
            balanceLabel.setText(currencyFormatter.format(saldo));
            
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
            depositoValorField.setValue(null);
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
            pixValorField.setValue(null);
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
            
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (Map<String, Object> t : transacoes) {
                Map<String, String> enviador = (Map<String, String>) t.get("usuario_enviador");
                Map<String, String> recebedor = (Map<String, String>) t.get("usuario_recebedor");
                
                if (enviador == null || enviador.get("cpf") == null || recebedor == null || recebedor.get("nome") == null) {
                    System.err.println("Registro de transação malformado recebido: " + t);
                    continue; 
                }
                
                boolean isEnviada = enviador.get("cpf").equals(this.cpfAtual);
                
                String dataFormatada;
                try {
                    String dataStr = (String) t.get("criado_em");
                    if (dataStr == null) {
                        dataFormatada = "Data inválida";
                    } else {
                        Date date = isoFormat.parse(dataStr);
                        dataFormatada = displayFormat.format(date);
                    }
                } catch (ParseException e) {
                    dataFormatada = (String) t.get("criado_em"); 
                } catch (Exception e) {
                    dataFormatada = "Data N/A";
                }
                
                String descricao = isEnviada
                    ? "PIX Enviado para " + recebedor.get("nome")
                    : "PIX Recebido de " + enviador.get("nome");
                
                double valor = 0.0;
                Object valorObj = t.get("valor_enviado");
                if (valorObj instanceof Number) {
                    valor = ((Number) valorObj).doubleValue();
                }
                    
                extratoTableModel.addRow(new Object[]{ isEnviada, dataFormatada, descricao, valor });
            }
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao buscar extrato: " + response.get("info"), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class ExtratoCellRenderer extends DefaultTableCellRenderer {
        private final Color VERDE = new Color(0, 153, 51);
        private final Color VERMELHO = new Color(204, 0, 0);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            try {
                boolean isEnviada = (Boolean) table.getModel().getValueAt(row, 0);
                double valor = (Double) table.getModel().getValueAt(row, 3);
                
                setForeground(Color.BLACK);
                setHorizontalAlignment(SwingConstants.LEFT);
                setFont(getFont().deriveFont(Font.PLAIN));

                switch (column) {
                    case 0: 
                        setText(isEnviada ? "↑" : "↓");
                        setForeground(isEnviada ? VERMELHO : VERDE);
                        setFont(getFont().deriveFont(Font.BOLD, 18f));
                        setHorizontalAlignment(SwingConstants.CENTER);
                        break;
                    case 1: 
                        setText(table.getModel().getValueAt(row, 1).toString());
                        break; 
                    case 2: 
                        setText(table.getModel().getValueAt(row, 2).toString());
                        break;
                    case 3: 
                        String prefixo = isEnviada ? "- " : "+ ";
                        setText(prefixo + currencyFormatter.format(valor));
                        setForeground(isEnviada ? VERMELHO : VERDE);
                        setHorizontalAlignment(SwingConstants.RIGHT);
                        setFont(getFont().deriveFont(Font.BOLD));
                        break;
                }
            
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : CINZA_FUNDO_PAINEL);
                }
                
            } catch (Exception e) {
                 setText(value != null ? value.toString() : "");
                 setBackground(Color.LIGHT_GRAY);
            }

            return this;
        }
    }

    public static class PlaceholderUtil {
        public static void addPlaceholder(JTextComponent component, String placeholder) {
            Color placeholderColor = Color.LIGHT_GRAY;
            Color defaultColor = component.getForeground();
            
            if(component.getText().isEmpty() || (component instanceof JFormattedTextField && ((JFormattedTextField)component).getValue() == null)){
                component.setText(placeholder);
                component.setForeground(placeholderColor);
            }

            component.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (component.getForeground() == placeholderColor) {
                        component.setText("");
                        component.setForeground(defaultColor);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    String text = component.getText().trim();
                     if (component instanceof JFormattedTextField) {
                        JFormattedTextField ftf = (JFormattedTextField) component;
                        if (ftf.getValue() == null || ftf.getText().matches("[\\s.R$\\-,]*")) {
                           component.setForeground(placeholderColor);
                           component.setText(placeholder);
                        }
                    } else if (text.isEmpty()) {
                        component.setForeground(placeholderColor);
                        component.setText(placeholder);
                    }
                }
            });
        }
    }
}