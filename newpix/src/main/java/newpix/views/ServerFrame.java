package newpix.views;

import newpix.Servidor.ClientInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Vector;

public class ServerFrame extends JFrame {

    // --- NOVO: Padrões de design ---
    private static final Color ROXO_PRINCIPAL = new Color(95, 49, 230);
    private static final Color CINZA_FUNDO = new Color(245, 245, 245);
    private static final Color CINZA_BORDA = new Color(220, 220, 220);
    private static final Color VERDE_ONLINE = new Color(0, 184, 148);

    private final DefaultTableModel connectedClientsModel;
    private final JTextArea logArea;
    private final Vector<ClientInfo> clientList = new Vector<>();
    
    // --- NOVO: Labels para o painel de status ---
    private final JLabel statusLabel;
    private final JLabel portLabel;
    private final JLabel clientsCountLabel;

    public ServerFrame() {
        setTitle("NewPix - Painel do Servidor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 950, 650);
        setLocationRelativeTo(null);

        // --- PAINEL PRINCIPAL ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(CINZA_FUNDO);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // --- PAINEL SUPERIOR (STATUS) ---
        JPanel statusPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(new MatteBorder(0, 0, 1, 0, CINZA_BORDA));
        
        statusLabel = createStatusCard("Status", "Online", VERDE_ONLINE);
        portLabel = createStatusCard("Porta", "Aguardando...", Color.DARK_GRAY);
        clientsCountLabel = createStatusCard("Clientes Conectados", "0", ROXO_PRINCIPAL);

        statusPanel.add(statusLabel);
        statusPanel.add(portLabel);
        statusPanel.add(clientsCountLabel);
        
        mainPanel.add(statusPanel, BorderLayout.NORTH);

        // --- PAINEL INFERIOR (CLIENTES E LOGS) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.4); // 40% para a esquerda, 60% para a direita
        splitPane.setBorder(null);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // --- PAINEL DE CLIENTES CONECTADOS ---
        JPanel clientsPanel = new JPanel(new BorderLayout());
        clientsPanel.setBackground(Color.WHITE);
        clientsPanel.setBorder(new MatteBorder(1, 1, 1, 1, CINZA_BORDA));
        
        JLabel clientsTitle = new JLabel("Clientes Conectados");
        clientsTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        clientsTitle.setBorder(new EmptyBorder(10,10,10,10));
        clientsPanel.add(clientsTitle, BorderLayout.NORTH);

        String[] columnNames = {"IP", "Porta", "CPF", "Conexão"};
        connectedClientsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable clientsTable = new JTable(connectedClientsModel);
        clientsTable.setRowHeight(25);
        clientsTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        clientsTable.setGridColor(CINZA_BORDA);
        
        JTableHeader header = clientsTable.getTableHeader();
        header.setBackground(CINZA_FUNDO);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));

        clientsPanel.add(new JScrollPane(clientsTable), BorderLayout.CENTER);
        splitPane.setLeftComponent(clientsPanel);

        // --- PAINEL DE LOGS ---
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(Color.WHITE);
        logPanel.setBorder(new MatteBorder(1, 1, 1, 1, CINZA_BORDA));

        JLabel logsTitle = new JLabel("Logs de Requisições");
        logsTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        logsTitle.setBorder(new EmptyBorder(10,10,10,10));
        logPanel.add(logsTitle, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        logArea.setMargin(new Insets(5, 5, 5, 5));
        
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(null);
        logPanel.add(logScrollPane, BorderLayout.CENTER);
        splitPane.setRightComponent(logPanel);
    }
    
    // --- NOVO: Método para criar os cards de status ---
    private JLabel createStatusCard(String title, String value, Color valueColor) {
        JLabel label = new JLabel("<html><div style='text-align: center; padding: 10px;'>"
                               + "<span style='font-size: 11px; color: #888888;'>" + title.toUpperCase() + "</span><br>"
                               + "<b style='font-size: 18px; color: " + String.format("#%06x", valueColor.getRGB() & 0xFFFFFF) + ";'>" + value + "</b>"
                               + "</div></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    public void setPort(int port) {
        portLabel.setText("<html><div style='text-align: center; padding: 10px;'>"
                        + "<span style='font-size: 11px; color: #888888;'>PORTA</span><br>"
                        + "<b style='font-size: 18px;'>" + port + "</b>"
                        + "</div></html>");
    }

    public void addClient(ClientInfo clientInfo) {
        SwingUtilities.invokeLater(() -> {
            clientList.add(clientInfo);
            connectedClientsModel.addRow(clientInfo.toTableRow());
            updateClientCount();
        });
    }

    public void removeClient(ClientInfo clientInfo) {
        SwingUtilities.invokeLater(() -> {
            int indexToRemove = clientList.indexOf(clientInfo);
            if (indexToRemove != -1) {
                clientList.remove(indexToRemove);
                connectedClientsModel.removeRow(indexToRemove);
                updateClientCount();
            }
        });
    }
    
    public void updateClientCpf(ClientInfo clientInfo, String cpf) {
        SwingUtilities.invokeLater(() -> {
            int indexToUpdate = clientList.indexOf(clientInfo);
            if (indexToUpdate != -1) {
                clientList.get(indexToUpdate).setCpf(cpf);
                connectedClientsModel.setValueAt(cpf, indexToUpdate, 2);
            }
        });
    }

    public void addLog(String logMessage) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(logMessage + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    private void updateClientCount() {
        clientsCountLabel.setText("<html><div style='text-align: center; padding: 10px;'>"
                                + "<span style='font-size: 11px; color: #888888;'>CLIENTES CONECTADOS</span><br>"
                                + "<b style='font-size: 18px; color: " + String.format("#%06x", ROXO_PRINCIPAL.getRGB() & 0xFFFFFF) + ";'>" + clientList.size() + "</b>"
                                + "</div></html>");
    }
}