package newpix.views;

import newpix.Servidor.ClientInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class ServerFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private final DefaultTableModel connectedClientsModel;
    private final JTextArea logArea;
    // Lista para manter a referência dos objetos ClientInfo e facilitar a busca
    private final Vector<ClientInfo> clientList = new Vector<>();

    public ServerFrame() {
        setTitle("Servidor NewPix - Status");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        setLocationRelativeTo(null);

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        // --- Painel de Clientes Conectados com Tabela ---
        JPanel clientsPanel = new JPanel(new BorderLayout());
        clientsPanel.setBorder(BorderFactory.createTitledBorder("Clientes Conectados"));
        
        String[] columnNames = {"IP", "Porta", "CPF", "Conexão"};
        connectedClientsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna as células da tabela não editáveis
            }
        };

        JTable clientsTable = new JTable(connectedClientsModel);
        clientsTable.setFillsViewportHeight(true);
        clientsPanel.add(new JScrollPane(clientsTable), BorderLayout.CENTER);
        clientsPanel.setPreferredSize(new Dimension(400, 0));
        contentPane.add(clientsPanel, BorderLayout.WEST);

        // --- Painel de Logs ---
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Logs de Requisições"));
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        contentPane.add(logPanel, BorderLayout.CENTER);
    }

    public void addClient(ClientInfo clientInfo) {
        SwingUtilities.invokeLater(() -> {
            clientList.add(clientInfo);
            connectedClientsModel.addRow(clientInfo.toTableRow());
        });
    }

    public void removeClient(ClientInfo clientInfo) {
        SwingUtilities.invokeLater(() -> {
            int indexToRemove = clientList.indexOf(clientInfo);
            if (indexToRemove != -1) {
                clientList.remove(indexToRemove);
                connectedClientsModel.removeRow(indexToRemove);
            }
        });
    }
    
    public void updateClientCpf(ClientInfo clientInfo, String cpf) {
        SwingUtilities.invokeLater(() -> {
            int indexToUpdate = clientList.indexOf(clientInfo);
            if (indexToUpdate != -1) {
                // Atualiza o objeto na lista e o valor na célula da tabela
                clientList.get(indexToUpdate).setCpf(cpf);
                connectedClientsModel.setValueAt(cpf, indexToUpdate, 2); // Coluna 2 é o CPF
            }
        });
    }

    public void addLog(String logMessage) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(logMessage + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}