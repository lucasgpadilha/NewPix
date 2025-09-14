package newpix.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ServerFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private final DefaultListModel<String> connectedClientsModel;
    private final JTextArea logArea;

    public ServerFrame() {
        setTitle("Servidor NewPix - Status");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        // Painel de Clientes Conectados
        JPanel clientsPanel = new JPanel(new BorderLayout());
        clientsPanel.setBorder(BorderFactory.createTitledBorder("Clientes Conectados"));
        connectedClientsModel = new DefaultListModel<>();
        JList<String> connectedClientsList = new JList<>(connectedClientsModel);
        clientsPanel.add(new JScrollPane(connectedClientsList), BorderLayout.CENTER);
        clientsPanel.setPreferredSize(new Dimension(150, 0));
        contentPane.add(clientsPanel, BorderLayout.WEST);

        // Painel de Logs
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Logs de Requisições"));
        logArea = new JTextArea();
        logArea.setEditable(false);
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        contentPane.add(logPanel, BorderLayout.CENTER);
    }

    public void addClient(String clientIp) {
        SwingUtilities.invokeLater(() -> connectedClientsModel.addElement(clientIp));
    }

    public void removeClient(String clientIp) {
        SwingUtilities.invokeLater(() -> connectedClientsModel.removeElement(clientIp));
    }

    public void addLog(String logMessage) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(logMessage + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength()); // Auto-scroll
        });
    }
}