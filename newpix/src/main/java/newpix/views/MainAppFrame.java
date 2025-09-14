package newpix.views;

import newpix.controllers.JsonController;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

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

    /**
     * Processa as respostas recebidas do servidor enquanto esta janela estiver ativa.
     * @param jsonResponse A resposta do servidor em formato JSON.
     */
    public void handleServerResponse(String jsonResponse) {
        // Converte o JSON para um mapa para facilitar o acesso aos dados
        Map<String, Object> response = JsonController.fromJson(jsonResponse, new com.fasterxml.jackson.core.type.TypeReference<>() {});
        String operacao = (String) response.get("operacao");
        
        System.out.println("MainAppFrame recebeu a operação: " + operacao);

        // Adicione aqui a lógica para cada tipo de resposta
        // Ex: if ("usuario_ler".equals(operacao)) { atualizarSaldo(...); }
    }
}