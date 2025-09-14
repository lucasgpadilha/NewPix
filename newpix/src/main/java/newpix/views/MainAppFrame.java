package newpix.views;

import javax.swing.*;
import java.awt.*;

public class MainAppFrame extends JFrame {
    public MainAppFrame() {
        setTitle("NewPix - Sua Conta");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Adicionar painéis para saldo, transferir, extrato, etc.
        JLabel welcomeLabel = new JLabel("Bem-vindo à sua conta NewPix!", SwingConstants.CENTER);
        add(welcomeLabel);
    }
}