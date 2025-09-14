package newpix.views;

import javax.swing.*;
import java.awt.*;

public class HomeFrame extends JFrame {
    public HomeFrame() {
        setTitle("Bem-vindo ao NewPix");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JButton btnLogin = new JButton("Login");
        JButton btnCadastro = new JButton("Cadastrar");
        JButton btnSair = new JButton("Sair");

        add(btnLogin);
        add(btnCadastro);
        add(btnSair);

        btnLogin.addActionListener(e -> {
            LoginFrame loginFrame = new LoginFrame(this);
            loginFrame.setVisible(true);
            this.setVisible(false);
        });

        btnCadastro.addActionListener(e -> {
            CadastroFrame cadastroFrame = new CadastroFrame(this);
            cadastroFrame.setVisible(true);
            this.setVisible(false);
        });

        btnSair.addActionListener(e -> System.exit(0));
    }
}