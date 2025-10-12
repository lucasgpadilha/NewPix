import com.fasterxml.jackson.core.type.TypeReference;
import newpix.controllers.JsonController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ServerTestGUI extends JFrame {

    private final JTextArea logArea;
    private final JPanel testCasesPanel;
    private final JButton startButton;
    private final JLabel scoreLabel;
    private final JTextField ipField, portField;
    private final JTextField nome1Field, cpf1Field, senha1Field;
    private final JTextField nome2Field, cpf2Field, senha2Field;

    private final List<TestCasePanel> testCasePanels = new ArrayList<>();

    public ServerTestGUI() {
        setTitle("Ferramenta de Avaliação Automatizada - NewPix Server");
        setSize(950, 850);
        setMinimumSize(new Dimension(950, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        // --- PAINEL SUPERIOR: Configurações ---
        JPanel topConfigPanel = new JPanel();
        topConfigPanel.setLayout(new BoxLayout(topConfigPanel, BoxLayout.Y_AXIS));

        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionPanel.setBorder(new TitledBorder("1. Configurações do Servidor"));
        ipField = new JTextField("127.0.0.1", 15);
        portField = new JTextField("20000", 6);
        connectionPanel.add(new JLabel("IP:"));
        connectionPanel.add(ipField);
        connectionPanel.add(new JLabel("Porta:"));
        connectionPanel.add(portField);
        topConfigPanel.add(connectionPanel);

        JPanel usersDataPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        usersDataPanel.setBorder(new TitledBorder("2. Dados de Teste Editáveis"));

        JPanel user1Panel = createUserDataPanel("Usuário 1 (Remetente)");
        nome1Field = new JTextField("Usuário Remetente", 15);
        cpf1Field = new JTextField("999.999.999-01", 12);
        senha1Field = new JTextField("senha123", 10);
        addFieldsToPanel(user1Panel, nome1Field, cpf1Field, senha1Field, "Nome 1:", "CPF 1:", "Senha 1:");

        JPanel user2Panel = createUserDataPanel("Usuário 2 (Destinatário)");
        nome2Field = new JTextField("Usuário Destinatário", 15);
        cpf2Field = new JTextField("888.888.888-02", 12);
        senha2Field = new JTextField("senha456", 10);
        addFieldsToPanel(user2Panel, nome2Field, cpf2Field, senha2Field, "Nome 2:", "CPF 2:", "Senha 2:");

        usersDataPanel.add(user1Panel);
        usersDataPanel.add(user2Panel);
        topConfigPanel.add(usersDataPanel);
        mainPanel.add(topConfigPanel, BorderLayout.NORTH);

        testCasesPanel = new JPanel();
        testCasesPanel.setLayout(new BoxLayout(testCasesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPaneTests = new JScrollPane(testCasesPanel);
        scrollPaneTests.setBorder(BorderFactory.createTitledBorder("3. Cenários de Teste (Ative, defina a nota e execute)"));
        mainPanel.add(scrollPaneTests, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        startButton = new JButton("Iniciar Testes de Avaliação");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        startButton.addActionListener(e -> runTests());

        scoreLabel = new JLabel("Nota Final: 0 / 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JPanel startScorePanel = new JPanel(new BorderLayout(10, 0));
        startScorePanel.add(startButton, BorderLayout.CENTER);
        startScorePanel.add(scoreLabel, BorderLayout.EAST);
        bottomPanel.add(startScorePanel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPaneLogs = new JScrollPane(logArea);
        scrollPaneLogs.setPreferredSize(new Dimension(800, 200));
        bottomPanel.add(scrollPaneLogs, BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        initializeTestCases();
    }

    private JPanel createUserDataPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    private void addFieldsToPanel(JPanel panel, JTextField name, JTextField cpf, JTextField pass, String nameLabel, String cpfLabel, String passLabel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel(nameLabel), gbc);
        gbc.gridy = 1; panel.add(new JLabel(cpfLabel), gbc);
        gbc.gridy = 2; panel.add(new JLabel(passLabel), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(name, gbc);
        gbc.gridy = 1; panel.add(cpf, gbc);
        gbc.gridy = 2; panel.add(pass, gbc);
    }
    
    private void addSectionTitle(String title) {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(200, 220, 255));
        titlePanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, titleLabel.getPreferredSize().height + 12));
        testCasesPanel.add(titlePanel);
    }
    
    private void addTestCase(int index, String description, boolean expectSuccess, int points, TestAction action) {
        TestCasePanel panel = new TestCasePanel(index, description, expectSuccess, points, action);
        this.testCasePanels.add(panel);
        this.testCasesPanel.add(panel);
    }

    private void initializeTestCases() {
        testCasePanels.clear();
        testCasesPanel.removeAll();
        int testIndex = 0;
        
        // --- AVALIAÇÃO 1 ---
        addSectionTitle("Avaliação 1 - Servidor (CRUD e Autenticação)");
        addTestCase(testIndex++, "Realizar cadastro de usuário (C)", true, 10, () -> TestActions.criarUsuario(TestActions.user1));
        addTestCase(testIndex++, "Realizar login do cliente", true, 10, () -> { TestActions.user1.token = TestActions.login(TestActions.user1.cpf, TestActions.user1.senha); return TestActions.user1.token != null; });
        addTestCase(testIndex++, "Enviar os dados do usuário (R)", true, 10, () -> TestActions.verificarSaldo(TestActions.user1.token, 0.0));
        addTestCase(testIndex++, "Atualizar dados do cadastro (U)", true, 10, () -> TestActions.atualizarUsuario(TestActions.user1.token, "Novo Nome Teste", null));
        addTestCase(testIndex++, "Realizar logout do cliente", true, 10, () -> TestActions.logout(TestActions.user1.token));
        
        // --- AVALIAÇÃO 2 ---
        addSectionTitle("Avaliação 2 - Endpoints Financeiros");
        addTestCase(testIndex++, "Setup: Criar e Logar Usuário 2", true, 0, () -> { TestActions.criarUsuario(TestActions.user2); TestActions.user2.token = TestActions.login(TestActions.user2.cpf, TestActions.user2.senha); return TestActions.user2.token != null; });
        addTestCase(testIndex++, "Processar depósito na conta", true, 30, () -> TestActions.depositar(TestActions.user1.token, 100.00));
        addTestCase(testIndex++, "Processar nova transação (PIX)", true, 40, () -> TestActions.criarTransacao(TestActions.user1.token, TestActions.user2.cpf, 50.0));
        addTestCase(testIndex++, "Enviar extrato de transações", true, 30, () -> TestActions.lerTransacoes(TestActions.user1.token, 50.0));

        // --- AVALIAÇÃO 3 ---
        addSectionTitle("Avaliação 3 - Tratamento de Erros");
        addTestCase(testIndex++, "Erro: Tentar criar usuário com CPF duplicado", false, 20, () -> TestActions.criarUsuario(TestActions.user1));
        addTestCase(testIndex++, "Erro: Tentar login com senha errada", false, 20, () -> TestActions.login(TestActions.user1.cpf, "senha_errada") != null);
        addTestCase(testIndex++, "Erro: Transferir valor maior que o saldo", false, 20, () -> TestActions.criarTransacao(TestActions.user1.token, TestActions.user2.cpf, 9999.00));
        addTestCase(testIndex++, "Erro: Transferir para CPF inexistente", false, 20, () -> TestActions.criarTransacao(TestActions.user1.token, "000.000.000-00", 10.00));
        addTestCase(testIndex++, "Erro: Tentar usar token após logout", false, 20, () -> TestActions.verificarSaldo(TestActions.user1.token, 0.0));
        
        // --- LIMPEZA ---
        addSectionTitle("Finalização e Limpeza");
        addTestCase(testIndex++, "Apagar dados do usuário 1 (D)", true, 10, () -> { String tempToken = TestActions.login(TestActions.user1.cpf, "senha123"); return TestActions.deletarUsuario(tempToken); });
        addTestCase(testIndex++, "Apagar dados do usuário 2 (D)", true, 0, () -> TestActions.deletarUsuario(TestActions.user2.token));

        revalidate(); repaint();
    }

    private void runTests() {
        startButton.setEnabled(false); startButton.setText("Testando...");
        logArea.setText("");
        testCasePanels.forEach(p -> p.setStatus(TestCasePanel.Status.PENDENTE));
        
        TestActions.configureUsers(
            new UserData(nome1Field.getText(), cpf1Field.getText(), senha1Field.getText()),
            new UserData(nome2Field.getText(), cpf2Field.getText(), senha2Field.getText())
        );

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            int currentScore = 0; int maxScore = 0;
            @Override
            protected Void doInBackground() throws Exception {
                String ip = ipField.getText(); int port = Integer.parseInt(portField.getText());
                try (Socket socket = new Socket(ip, port);
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    TestActions.initialize(out, in, this::publish);
                    
                    publish("--- INICIANDO FASE DE LIMPEZA PRÉ-TESTE ---");
                    String token1 = TestActions.login(TestActions.user1.cpf, TestActions.user1.senha);
                    if(token1 != null) TestActions.deletarUsuario(token1);
                    String token2 = TestActions.login(TestActions.user2.cpf, TestActions.user2.senha);
                    if(token2 != null) TestActions.deletarUsuario(token2);
                    publish("--- FIM DA FASE DE LIMPEZA ---\n");
                    
                    for (TestCasePanel testCasePanel : testCasePanels) {
                        if (testCasePanel.isActive()) {
                            // Relogar usuário 1 se o token estiver nulo antes de um teste que precise dele
                            if (TestActions.user1.token == null && testCasePanel.getDescription().contains("token")) {
                                publish("--- Realizando login automático do Usuário 1 para continuar os testes ---");
                                TestActions.user1.token = TestActions.login(TestActions.user1.cpf, TestActions.user1.senha);
                            }
                            boolean passed = testCasePanel.run();
                            int points = testCasePanel.getPoints();
                            maxScore += points;
                            if (passed) currentScore += points;
                        } else {
                            testCasePanel.setStatus(TestCasePanel.Status.IGNORADO);
                        }
                    }
                } catch (Exception e) { publish("!!! ERRO CRÍTICO !!!\n" + e.getMessage()); e.printStackTrace(); }
                return null;
            }
            @Override
            protected void process(List<String> chunks) { for (String text : chunks) logArea.append(text + "\n"); }
            @Override
            protected void done() {
                startButton.setEnabled(true); startButton.setText("Iniciar Testes Novamente");
                scoreLabel.setText(String.format("Nota Final: %d / %d", currentScore, maxScore));
                TestActions.close();
            }
        };
        worker.execute();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception e) { e.printStackTrace(); }
            new ServerTestGUI().setVisible(true);
        });
    }
}

class TestCasePanel extends JPanel {
    enum Status { PENDENTE, RODANDO, SUCESSO, FALHA, IGNORADO }
    private final TestAction action; private final boolean expectSuccess; private final String description;
    private final JCheckBox activeCheckBox; private final JSpinner pointsSpinner; private final JLabel statusLabel;
    
    public TestCasePanel(int index, String description, boolean expectSuccess, int points, TestAction action) {
        this.action = action; this.expectSuccess = expectSuccess; this.description = description;
        setLayout(new BorderLayout(10, 0));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        setBorder(new EmptyBorder(4, 15, 4, 5));
        if (index % 2 == 1) setBackground(new Color(242, 242, 242));

        activeCheckBox = new JCheckBox(); activeCheckBox.setSelected(true); activeCheckBox.setOpaque(false);
        JLabel descriptionLabel = new JLabel(description); descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pointsSpinner = new JSpinner(new SpinnerNumberModel(points, 0, 100, 5));
        pointsSpinner.setMaximumSize(new Dimension(60, 30));
        statusLabel = new JLabel("PENDENTE", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        statusLabel.setOpaque(true); statusLabel.setPreferredSize(new Dimension(90, 25));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); leftPanel.setOpaque(false);
        leftPanel.add(activeCheckBox); leftPanel.add(descriptionLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); rightPanel.setOpaque(false);
        rightPanel.add(new JLabel("Nota:")); rightPanel.add(pointsSpinner); rightPanel.add(statusLabel);

        add(leftPanel, BorderLayout.CENTER); add(rightPanel, BorderLayout.EAST);
        setStatus(Status.PENDENTE);
    }
    public String getDescription() { return this.description; }
    public boolean isActive() { return activeCheckBox.isSelected(); }
    public int getPoints() { return (int) pointsSpinner.getValue(); }
    public boolean run() {
        setStatus(Status.RODANDO);
        try {
            boolean actualSuccess = action.execute();
            if (actualSuccess == expectSuccess) { setStatus(Status.SUCESSO); return true; }
            else { setStatus(Status.FALHA); return false; }
        } catch (Exception e) { Logger.getGlobal().warning("Test failed with exception: " + e.getMessage()); setStatus(Status.FALHA); return false; }
    }
    public void setStatus(Status newStatus) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(newStatus.name());
            switch (newStatus) {
                case PENDENTE: statusLabel.setBackground(Color.LIGHT_GRAY); statusLabel.setForeground(Color.BLACK); break;
                case RODANDO: statusLabel.setBackground(Color.ORANGE); statusLabel.setForeground(Color.BLACK); break;
                case SUCESSO: statusLabel.setBackground(new Color(40, 167, 69)); statusLabel.setForeground(Color.WHITE); break;
                case FALHA: statusLabel.setBackground(new Color(220, 53, 69)); statusLabel.setForeground(Color.WHITE); break;
                case IGNORADO: statusLabel.setBackground(Color.GRAY); statusLabel.setForeground(Color.WHITE); break;
            }
        });
    }
}

@FunctionalInterface interface TestAction { boolean execute() throws Exception; }
class UserData {
    String nome, cpf, senha, token;
    public UserData(String n, String c, String s) { nome=n; cpf=c; senha=s; }
}

class TestActions {
    private static PrintWriter out; private static BufferedReader in; private static java.util.function.Consumer<String> logger;
    static UserData user1; static UserData user2;
    public static void configureUsers(UserData u1, UserData u2) { user1 = u1; user2 = u2; }
    public static void initialize(PrintWriter o, BufferedReader i, java.util.function.Consumer<String> l) { out = o; in = i; logger = l; }
    public static void close() { out = null; in = null; logger = null; }
    private static Map<String, Object> sendAndReceive(String req) throws IOException {
        logger.accept("[ENVIO] " + req); out.println(req);
        Map<String, Object> reqMap = JsonController.fromJson(req, new TypeReference<>() {});
        String expectedOp = (String) reqMap.get("operacao");
        while(true) {
            String res = in.readLine(); logger.accept("[RESPOSTA] " + res);
            if (res == null) throw new IOException("O Servidor encerrou a conexão inesperadamente.");
            Map<String, Object> resMap = JsonController.fromJson(res, new TypeReference<>() {});
            String receivedOp = (String) resMap.get("operacao");
            if (expectedOp.equals(receivedOp)) return resMap;
            if ("notificacao_pix_recebido".equals(receivedOp)) { logger.accept("--- (Notificação recebida e ignorada pelo listener principal) ---"); continue; }
            throw new IOException("Erro de sincronia: Esperava '" + expectedOp + "' mas recebeu '" + receivedOp + "'");
        }
    }
    public static boolean criarUsuario(UserData user) throws Exception {
        Map<String, Object> req = new ConcurrentHashMap<>();
        req.put("operacao", "usuario_criar"); req.put("nome", user.nome); req.put("cpf", user.cpf); req.put("senha", user.senha);
        return (boolean) sendAndReceive(JsonController.toJson(req)).get("status");
    }
    public static String login(String cpf, String senha) throws Exception {
        Map<String, Object> req = new ConcurrentHashMap<>();
        req.put("operacao", "usuario_login"); req.put("cpf", cpf); req.put("senha", senha);
        Map<String, Object> res = sendAndReceive(JsonController.toJson(req));
        return (res != null && res.get("status") != null && (boolean) res.get("status")) ? (String) res.get("token") : null;
    }
    public static boolean depositar(String token, double valor) throws Exception {
        if (token == null) return false;
        Map<String, Object> req = new ConcurrentHashMap<>();
        req.put("operacao", "depositar"); req.put("token", token); req.put("valor_enviado", valor);
        return (boolean) sendAndReceive(JsonController.toJson(req)).get("status");
    }
    public static boolean verificarSaldo(String token, double saldoEsperado) throws Exception {
        if (token == null) return false;
        Map<String, Object> req = new ConcurrentHashMap<>();
        req.put("operacao", "usuario_ler"); req.put("token", token);
        Map<String, Object> res = sendAndReceive(JsonController.toJson(req));
        if (!(boolean) res.get("status")) return false;
        Map<String, Object> usr = (Map<String, Object>) res.get("usuario");
        return Math.abs(((Number) usr.get("saldo")).doubleValue() - saldoEsperado) < 0.001;
    }
    public static boolean criarTransacao(String token, String cpfDestino, double valor) throws Exception {
        if (token == null) return false;
        Map<String, Object> req = new ConcurrentHashMap<>();
        req.put("operacao", "transacao_criar"); req.put("token", token); req.put("cpf_destino", cpfDestino); req.put("valor", valor);
        return (boolean) sendAndReceive(JsonController.toJson(req)).get("status");
    }
    public static boolean lerTransacoes(String token, double v) throws Exception {
        if (token == null) return false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String df = sdf.format(new Date()); String di = sdf.format(new Date(System.currentTimeMillis() - 86400000));
        Map<String, Object> req = new ConcurrentHashMap<>();
        req.put("operacao", "transacao_ler"); req.put("token", token); req.put("data_inicial", di); req.put("data_final", df);
        Map<String, Object> res = sendAndReceive(JsonController.toJson(req));
        if (!(boolean) res.get("status")) return false;
        List<Map<String, Object>> txs = (List<Map<String, Object>>) res.get("transacoes");
        if (txs.isEmpty() && v > 0) return false;
        if (v == 0) return true;
        for (Map<String, Object> t : txs) {
            if (Math.abs(((Number) t.get("valor_enviado")).doubleValue() - v) < 0.001) return true;
        }
        return false;
    }
    public static boolean atualizarUsuario(String token, String n, String s) throws Exception {
        if (token == null) return false;
        Map<String, String> ud = new ConcurrentHashMap<>();
        if (n != null) ud.put("nome", n); if (s != null) ud.put("senha", s);
        Map<String, Object> req = new ConcurrentHashMap<>();
        req.put("operacao", "usuario_atualizar"); req.put("token", token); req.put("usuario", ud);
        return (boolean) sendAndReceive(JsonController.toJson(req)).get("status");
    }
    public static boolean logout(String token) throws Exception {
        if (token == null) return false;
        Map<String, Object> req = new ConcurrentHashMap<>();
        req.put("operacao", "usuario_logout"); req.put("token", token);
        return (boolean) sendAndReceive(JsonController.toJson(req)).get("status");
    }
    public static boolean deletarUsuario(String token) throws Exception {
        if (token == null) return false;
        Map<String, Object> req = new ConcurrentHashMap<>();
        req.put("operacao", "usuario_deletar"); req.put("token", token);
        return (boolean) sendAndReceive(JsonController.toJson(req)).get("status");
    }
}