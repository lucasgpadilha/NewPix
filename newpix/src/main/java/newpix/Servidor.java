package newpix;

import com.fasterxml.jackson.core.type.TypeReference;
import newpix.controllers.JsonController;
import newpix.views.ServerFrame;
import newpix.validator.Validator; // Importando o validador

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Servidor {
    private ServerSocket serverSocket;
    private final ServerFrame serverFrame;
    // Simulação de DAOs e Controllers
    // private final UsuarioController usuarioController = new UsuarioController();
    // private final SessaoController sessaoController = new SessaoController();
    // private final TransacaoController transacaoController = new TransacaoController();

    public Servidor(ServerFrame serverFrame) {
        this.serverFrame = serverFrame;
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            serverFrame.addLog(getTimestamp() + " Servidor NewPix iniciado na porta: " + port);
            while (true) {
                new ClientHandler(serverSocket.accept(), serverFrame).start();
            }
        } catch (IOException e) {
            serverFrame.addLog(getTimestamp() + " ERRO ao iniciar o servidor: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private final ServerFrame serverFrame;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket, ServerFrame serverFrame) {
            this.clientSocket = socket;
            this.serverFrame = serverFrame;
        }

        public void run() {
            String clientIp = clientSocket.getInetAddress().getHostAddress();
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                serverFrame.addClient(clientIp);
                serverFrame.addLog(getTimestamp() + " [CONEXÃO] Cliente conectado: " + clientIp);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    serverFrame.addLog(getTimestamp() + " [RECEBIDO] De " + clientIp + ": " + inputLine);
                    String responseJson = processarRequisicao(inputLine);
                    serverFrame.addLog(getTimestamp() + " [ENVIADO] Para " + clientIp + ": " + responseJson);
                    out.println(responseJson);
                }
            } catch (IOException e) {
                // Silencioso para desconexões normais
            } finally {
                serverFrame.removeClient(clientIp);
                serverFrame.addLog(getTimestamp() + " [DESCONEXÃO] Cliente desconectado: " + clientIp);
                try {
                    if (clientSocket != null) clientSocket.close();
                } catch (IOException e) {
                    // Ignorar
                }
            }
        }
        
        private String processarRequisicao(String jsonRequest) {
            Map<String, Object> responseMap = new ConcurrentHashMap<>();
            try {
                // 1. Validar a mensagem do cliente
                Validator.validateClient(jsonRequest);
                
                Map<String, Object> request = JsonController.fromJson(jsonRequest, new TypeReference<Map<String, Object>>() {});
                String operacao = (String) request.get("operacao");
                responseMap.put("operacao", operacao);

                // 2. Executar a lógica de negócio
                switch (operacao) {
                    case "usuario_login":
                        // Lógica de login aqui...
                        // Exemplo:
                        // Usuario user = usuarioController.login(request.get("cpf"), request.get("senha"));
                        // if (user != null) {
                        //     String token = sessaoController.createSessao(user.getId());
                        //     responseMap.put("status", true);
                        //     responseMap.put("info", "Login bem-sucedido.");
                        //     responseMap.put("token", token);
                        // } else {
                        //     responseMap.put("status", false);
                        //     responseMap.put("info", "CPF ou senha inválidos.");
                        // }
                        
                        // Resposta mockada para teste
                        responseMap.put("status", true);
                        responseMap.put("info", "Login bem-sucedido (simulado).");
                        responseMap.put("token", UUID.randomUUID().toString());
                        break;
                    
                    // Adicionar outros cases para 'usuario_criar', 'transacao_criar', etc.

                    default:
                        responseMap.put("status", false);
                        responseMap.put("info", "Operação desconhecida.");
                }

            } catch (Exception e) {
                // Erro de validação ou de processamento
                responseMap.put("status", false);
                responseMap.put("info", "Erro no servidor: " + e.getMessage());
            }

            // 3. Retornar a resposta em JSON
            return JsonController.toJson(responseMap);
        }
    }

    private static String getTimestamp() {
        return "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "]";
    }
}