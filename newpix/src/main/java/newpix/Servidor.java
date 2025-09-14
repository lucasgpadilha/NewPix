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
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Servidor {
    private ServerSocket serverSocket;
    private final ServerFrame serverFrame;
    private final UsuarioController usuarioController = new UsuarioController();
    private final SessaoController sessaoController = new SessaoController();

    // Classe interna para armazenar dados do cliente conectado
    public static class ClientInfo {
        public final String ip;
        public final int port;
        public final String connectionTime;
        public String cpf = "Não logado";
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        public ClientInfo(String ip, int port) {
            this.ip = ip;
            this.port = port;
            this.connectionTime = LocalDateTime.now().format(formatter);
        }

        public void setCpf(String cpf) {
            this.cpf = cpf;
        }

        // Método para converter os dados para uma linha da tabela
        public Object[] toTableRow() {
            return new Object[]{ip, port, cpf, connectionTime};
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClientInfo that = (ClientInfo) o;
            return port == that.port && ip.equals(that.ip);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ip, port);
        }
    }

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
        // ... (demais atributos)
        private final Socket clientSocket;
        private final ServerFrame serverFrame;
        private final Servidor servidor;
        private PrintWriter out;
        private BufferedReader in;
        private final ClientInfo clientInfo;

        public ClientHandler(Socket socket, ServerFrame serverFrame, Servidor servidor) {
            this.clientSocket = socket;
            this.serverFrame = serverFrame;
            this.servidor = servidor;
            this.clientInfo = new ClientInfo(socket.getInetAddress().getHostAddress(), socket.getPort());
        }

        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                serverFrame.addClient(clientInfo);
                serverFrame.addLog(getTimestamp() + " [CONEXÃO] Cliente conectado: " + clientInfo.ip + ":" + clientInfo.port);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    serverFrame.addLog(getTimestamp() + " [RECEBIDO] De " + clientInfo.ip + ":" + clientInfo.port + ": " + inputLine);
                    String responseJson = processarRequisicao(inputLine);
                    serverFrame.addLog(getTimestamp() + " [ENVIADO] Para " + clientInfo.ip + ":" + clientInfo.port + ": " + responseJson);
                    out.println(responseJson);
                }
            } catch (IOException e) {
                // Silencioso
            } finally {
                serverFrame.removeClient(clientInfo);
                serverFrame.addLog(getTimestamp() + " [DESCONEXÃO] Cliente desconectado: " + clientInfo.ip + ":" + clientInfo.port);
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
                    case "usuario_ler":
                        handleUsuarioLer(request, responseMap);
                        break;
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
                    
                    // Atualiza a tabela na tela do servidor com o CPF do usuário
                    serverFrame.updateClientCpf(this.clientInfo, cpf);

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
            // ... (lógica de cadastro inalterada)
        }

        private void handleUsuarioLer(Map<String, Object> request, Map<String, Object> responseMap) {
            try {
                String token = (String) request.get("token");
                Usuario user = servidor.sessaoController.getUsuarioPorToken(token);

                if (user != null) {
                    Map<String, Object> userData = new ConcurrentHashMap<>();
                    userData.put("cpf", user.getCpf());
                    userData.put("nome", user.getNome());
                    userData.put("saldo", user.getSaldo());
                    
                    responseMap.put("status", true);
                    responseMap.put("info", "Dados do usuário recuperados com sucesso.");
                    responseMap.put("usuario", userData);
                } else {
                    responseMap.put("status", false);
                    responseMap.put("info", "Token inválido ou sessão expirada.");
                }
            } catch (Exception e) {
                e.printStackTrace(); // Bom para debug no console do servidor
                responseMap.put("status", false);
                responseMap.put("info", "Erro interno ao buscar dados do usuário.");
            }
        }
    }

    private static String getTimestamp() {
        return "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "]";
    }
}