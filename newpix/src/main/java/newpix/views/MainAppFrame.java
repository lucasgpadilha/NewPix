package newpix.views;

import newpix.Cliente;
import newpix.controllers.JsonController;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainAppFrame extends JFrame {
    private JLabel welcomeLabel;
    private JLabel balanceLabel;

    public MainAppFrame() {
        setTitle("NewPix - Sua Conta");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Painel Superior: Boas-vindas e Saldo ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        welcomeLabel = new JLabel("Carregando seus dados...");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        balanceLabel = new JLabel("Saldo: R$ --,--");
        balanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));

        topPanel.add(welcomeLabel);
        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topPanel.add(balanceLabel);

        // --- Painel Central: Ações Principais ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        JButton pixButton = new JButton("Fazer um Pix");
        pixButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        JButton extratoButton = new JButton("Meu Extrato");
        extratoButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        centerPanel.add(pixButton);
        centerPanel.add(extratoButton);

        // --- Painel Inferior: Ações Secundárias ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton myDataButton = new JButton("Meus Dados");
        JButton logoutButton = new JButton("Logout");
        bottomPanel.add(myDataButton);
        bottomPanel.add(logoutButton);

        // --- Adicionando painéis ao Frame ---
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Ações dos botões (Listeners) ---
        logoutButton.addActionListener(e -> logout());
        
        // Exemplo de como as outras ações seriam chamadas
        pixButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Funcionalidade 'Fazer um Pix' ainda não implementada."));
        extratoButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Funcionalidade 'Meu Extrato' ainda não implementada."));
        myDataButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Funcionalidade 'Meus Dados' ainda não implementada."));

        // Informa ao singleton do cliente que esta é a janela ativa
        Cliente.getInstance().setMainAppFrame(this);
        // Busca os dados do usuário assim que a janela é criada
        loadUserData();
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
        // Implementar a chamada para `usuario_logout` se necessário.
        // Por enquanto, apenas fecha a tela e volta para a autenticação.
        this.dispose();
        new AuthenticationFrame().setVisible(true);
    }

    public void handleServerResponse(String jsonResponse) {
        Map<String, Object> response = JsonController.fromJson(jsonResponse, new com.fasterxml.jackson.core.type.TypeReference<>() {});
        String operacao = (String) response.get("operacao");
        
        if ("usuario_ler".equals(operacao)) {
            boolean status = (boolean) response.get("status");
            if (status) {
                Map<String, Object> usuario = (Map<String, Object>) response.get("usuario");
                String nome = (String) usuario.get("nome");
                Double saldo = (Double) usuario.get("saldo");

                welcomeLabel.setText("Bem-vindo(a), " + nome + "!");
                balanceLabel.setText(String.format("Saldo: R$ %.2f", saldo));
            } else {
                String info = (String) response.get("info");
                JOptionPane.showMessageDialog(this, "Não foi possível carregar seus dados: " + info, "Erro", JOptionPane.ERROR_MESSAGE);
                logout();
            }
        }
    }
}