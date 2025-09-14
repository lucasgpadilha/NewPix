package newpix.views;

import newpix.Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class HomeFrame extends JFrame {
    public HomeFrame() {
        setTitle("Bem-vindo ao NewPix");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        JButton btnLogin = new JButton("Login");
        JButton btnCadastro = new JButton("Cadastrar");
        JButton btnSair = new JButton("Sair");

        add(btnLogin);
        add(btnCadastro);
        add(btnSair);

        // Ações dos botões
        btnLogin.addActionListener(e -> {
            LoginFrame loginFrame = new LoginFrame(this);
            loginFrame.setVisible(true);
            this.setVisible(false);
        });

        btnCadastro.addActionListener(e -> {
            // Chamar a tela de cadastro
        });

        btnSair.addActionListener(e -> System.exit(0));
    }
}