package newpix.views;

import newpix.Cliente;
import newpix.controllers.JsonController;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CadastroFrame extends JFrame {
    private final JTextField nomeField, cpfField;
    private final JPasswordField senhaField;
    private final JButton btnCadastrar;
    private final JFrame parentFrame;
    private final JTextField ipField;
    private final JTextField portaField;

    public CadastroFrame(JFrame parent) {
        this.parentFrame = parent;
        setTitle("NewPix - Cadastro");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("IP do Servidor:"));
        ipField = new JTextField("127.0.0.1");
        panel.add(ipField);

        panel.add(new JLabel("Porta do Servidor:"));
        portaField = new JTextField("12345");
        panel.add(portaField);

        panel.add(new JLabel("Nome Completo:"));
        nomeField = new JTextField();
        panel.add(nomeField);

        panel.add(new JLabel("CPF (000.000.000-00):"));
        cpfField = new JTextField();
        panel.add(cpfField);

        panel.add(new JLabel("Senha (min. 6 caracteres):"));
        senhaField = new JPasswordField();
        panel.add(senhaField);

        btnCadastrar = new JButton("Confirmar Cadastro");
        JButton btnVoltar = new JButton("Voltar");
        panel.add(btnCadastrar);
        panel.add(btnVoltar);

        add(panel);

        btnCadastrar.addActionListener(e -> attemptCadastro());
        btnVoltar.addActionListener(e -> {
            dispose();
            parent.setVisible(true);
        });
    }

    private void attemptCadastro() {
        String ip = ipField.getText();
        String portaStr = portaField.getText();
        try {
            int porta = Integer.parseInt(portaStr);
            Cliente cliente = Cliente.getInstance();
            if (!cliente.isConnected()) {
                cliente.startConnection(ip, porta);
            }
            cliente.setCadastroFrame(this);

            Map<String, String> request = new HashMap<>();
            request.put("operacao", "usuario_criar");
            request.put("nome", nomeField.getText());
            request.put("cpf", cpfField.getText());
            request.put("senha", new String(senhaField.getPassword()));
            cliente.sendMessage(JsonController.toJson(request));

            btnCadastrar.setText("Aguardando...");
            btnCadastrar.setEnabled(false);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro de conexão: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Porta inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleServerResponse(String jsonResponse) {
        Map<String, Object> response = JsonController.fromJson(jsonResponse, new com.fasterxml.jackson.core.type.TypeReference<>() {});
        boolean status = (boolean) response.get("status");
        String info = (String) response.get("info");

        JOptionPane.showMessageDialog(this, info, status ? "Sucesso" : "Erro", status ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

        if (status) {
            dispose(); // Fecha a tela de cadastro
            parentFrame.setVisible(true); // Volta para a tela Home
        } else {
            btnCadastrar.setText("Confirmar Cadastro");
            btnCadastrar.setEnabled(true);
        }
    }
}