package newpix;

import com.fasterxml.jackson.core.type.TypeReference;
import newpix.controllers.JsonController;
import newpix.controllers.SessaoController;
import newpix.controllers.UsuarioController;
import newpix.models.Usuario;
import newpix.validator.Validator;
import newpix.views.ServerFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Servidor {
    private ServerSocket serverSocket;
    private final ServerFrame serverFrame;
    private final UsuarioController usuarioController = new UsuarioController();
    private final SessaoController sessaoController = new SessaoController();

    public Servidor(ServerFrame serverFrame) {
        this.serverFrame = serverFrame;
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            serverFrame.addLog(getTimestamp() + " Servidor NewPix iniciado na porta: " + port);
            while (true) {
                new ClientHandler(serverSocket.accept(), serverFrame, this).start();
            }
        } catch (IOException e) {
            serverFrame.addLog(getTimestamp() + " ERRO ao iniciar o servidor: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;
        private final ServerFrame serverFrame;
        private final Servidor servidor;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket, ServerFrame serverFrame, Servidor servidor) {
            this.clientSocket = socket;
            this.serverFrame = serverFrame;
            this.servidor = servidor;
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
                // Silencioso
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
            String operacao = "desconhecida";
            try {
                Map<String, Object> request = JsonController.fromJson(jsonRequest, new TypeReference<>() {});
                operacao = (String) request.get("operacao");
                responseMap.put("operacao", operacao);

                Validator.validateClient(jsonRequest);

                switch (operacao) {
                    case "usuario_login":
                        handleLogin(request, responseMap);
                        break;
                    case "usuario_criar":
                        handleCadastro(request, responseMap);
                        break;
                    // Adicionar outros cases aqui
                    default:
                        responseMap.put("status", false);
                        responseMap.put("info", "Operação não implementada.");
                }
            } catch (Exception e) {
                responseMap.put("operacao", operacao);
                responseMap.put("status", false);
                responseMap.put("info", "Erro no servidor: " + e.getMessage());
            }
            return JsonController.toJson(responseMap);
        }

        private void handleLogin(Map<String, Object> request, Map<String, Object> responseMap) {
            try {
                String cpf = (String) request.get("cpf");
                String senha = (String) request.get("senha");
                Usuario user = servidor.usuarioController.login(cpf, senha);

                if (user != null) {
                    String token = servidor.sessaoController.criarSessao(user.getId());
                    responseMap.put("status", true);
                    responseMap.put("info", "Login bem-sucedido.");
                    responseMap.put("token", token);
                } else {
                    responseMap.put("status", false);
                    responseMap.put("info", "CPF ou senha inválidos.");
                }
            } catch (Exception e) {
                responseMap.put("status", false);
                responseMap.put("info", "Erro interno no login.");
            }
        }
        
        private void handleCadastro(Map<String, Object> request, Map<String, Object> responseMap) {
            try {
                String nome = (String) request.get("nome");
                String cpf = (String) request.get("cpf");
                String senha = (String) request.get("senha");

                if(servidor.usuarioController.getUsuarioPorCpf(cpf) != null){
                     responseMap.put("status", false);
                     responseMap.put("info", "CPF já cadastrado.");
                     return;
                }

                Usuario novoUsuario = new Usuario(0, nome, cpf, senha, 0);
                servidor.usuarioController.cadastrarUsuario(novoUsuario);

                responseMap.put("status", true);
                responseMap.put("info", "Usuário criado com sucesso.");

            } catch (Exception e) {
                responseMap.put("status", false);
                responseMap.put("info", "Erro ao criar usuário: " + e.getMessage());
            }
        }
    }

    private static String getTimestamp() {
        return "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "]";
    }
}