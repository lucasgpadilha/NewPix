package newpix;

import com.fasterxml.jackson.core.type.TypeReference;
import newpix.controllers.JsonController;
import newpix.controllers.SessaoController;
import newpix.controllers.TransacaoController;
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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Servidor {
    private ServerSocket serverSocket;
    private final ServerFrame serverFrame;
    private final UsuarioController usuarioController = new UsuarioController();
    private final SessaoController sessaoController = new SessaoController();
    private final TransacaoController transacaoController = new TransacaoController();

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

    private class ClientHandler extends Thread {
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
                    case "usuario_atualizar":
                        handleUsuarioAtualizar(request, responseMap);
                        break;
                    case "usuario_deletar":
                        handleUsuarioDeletar(request, responseMap);
                        break;
                    case "depositar":
                        handleDepositar(request, responseMap);
                        break;
                    case "transacao_criar":
                        handleTransacaoCriar(request, responseMap);
                        break;
                    case "transacao_ler":
                        handleTransacaoLer(request, responseMap);
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
                    serverFrame.updateClientCpf(this.clientInfo, cpf);
                } else {
                    responseMap.put("status", false);
                    responseMap.put("info", "CPF ou senha inválidos.");
                }
            } catch (Exception e) {
                responseMap.put("status", false);
                responseMap.put("info", "Erro interno no login: " + e.getMessage());
            }
        }
        
        private void handleCadastro(Map<String, Object> request, Map<String, Object> responseMap) {
            try {
                String nome = (String) request.get("nome");
                String cpf = (String) request.get("cpf");
                String senha = (String) request.get("senha");

                if (servidor.usuarioController.getUsuarioPorCpf(cpf) != null) {
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
                responseMap.put("status", false);
                responseMap.put("info", "Erro interno ao buscar dados do usuário: " + e.getMessage());
            }
        }

        private void handleUsuarioAtualizar(Map<String, Object> request, Map<String, Object> responseMap) {
             try {
                String token = (String) request.get("token");
                @SuppressWarnings("unchecked")
                Map<String, String> usuarioUpdateData = (Map<String, String>) request.get("usuario");
                String senhaAtual = usuarioUpdateData.get("senha_atual");

                Usuario user = servidor.usuarioController.validarCredenciaisPorToken(token, senhaAtual);

                if (user != null) {
                    String novoNome = usuarioUpdateData.get("nome");
                    String novaSenha = usuarioUpdateData.get("senha");
                    
                    if (novoNome != null && !novoNome.trim().isEmpty()) {
                        user.setNome(novoNome);
                    }
                    if (novaSenha != null && !novaSenha.trim().isEmpty()) {
                        user.setSenha(novaSenha);
                    }
                    
                    servidor.usuarioController.atualizarUsuario(user);
                    responseMap.put("status", true);
                    responseMap.put("info", "Dados atualizados com sucesso.");
                } else {
                    responseMap.put("status", false);
                    responseMap.put("info", "Senha atual incorreta ou token inválido.");
                }
            } catch (Exception e) {
                responseMap.put("status", false);
                responseMap.put("info", "Erro interno ao atualizar usuário: " + e.getMessage());
            }
        }
        
        private void handleUsuarioDeletar(Map<String, Object> request, Map<String, Object> responseMap) {
            try {
                String token = (String) request.get("token");
                String senha = (String) request.get("senha");
                
                Usuario user = servidor.usuarioController.validarCredenciaisPorToken(token, senha);

                if (user != null) {
                    servidor.sessaoController.deletarSessoesPorUsuario(user.getId());
                    servidor.usuarioController.deletarUsuario(user.getId());
                    
                    responseMap.put("status", true);
                    responseMap.put("info", "Sua conta foi deletada com sucesso.");
                } else {
                    responseMap.put("status", false);
                    responseMap.put("info", "Senha incorreta ou token inválido.");
                }
            } catch (Exception e) {
                responseMap.put("status", false);
                responseMap.put("info", "Erro interno ao deletar conta: " + e.getMessage());
            }
        }

        private void handleDepositar(Map<String, Object> request, Map<String, Object> responseMap) {
             try {
                String token = (String) request.get("token");
                double valor = ((Number) request.get("valor_enviado")).doubleValue();

                Usuario user = servidor.sessaoController.getUsuarioPorToken(token);

                if (user != null && valor > 0) {
                    servidor.usuarioController.depositar(user.getId(), valor);
                    responseMap.put("status", true);
                    responseMap.put("info", String.format("Depósito de R$ %.2f realizado com sucesso.", valor));
                } else {
                    responseMap.put("status", false);
                    responseMap.put("info", "Token inválido ou valor de depósito incorreto.");
                }
            } catch (Exception e) {
                responseMap.put("status", false);
                responseMap.put("info", "Erro interno ao processar depósito: " + e.getMessage());
            }
        }
        
        private void handleTransacaoCriar(Map<String, Object> request, Map<String, Object> responseMap) {
            try {
                String token = (String) request.get("token");
                String cpfDestino = (String) request.get("cpf_destino");
                double valor = ((Number) request.get("valor")).doubleValue();

                Usuario remetente = servidor.sessaoController.getUsuarioPorToken(token);
                
                if (remetente == null) {
                    responseMap.put("status", false);
                    responseMap.put("info", "Sua sessão é inválida. Faça login novamente.");
                    return;
                }
                
                servidor.transacaoController.realizarTransferencia(remetente.getId(), cpfDestino, valor);
                
                responseMap.put("status", true);
                responseMap.put("info", "Transferência PIX realizada com sucesso!");

            } catch (SQLException e) {
                // AGORA a mensagem de erro específica é retornada!
                responseMap.put("status", false);
                responseMap.put("info", e.getMessage());
            } catch (Exception e) {
                responseMap.put("status", false);
                responseMap.put("info", "Ocorreu um erro inesperado ao processar a transferência.");
            }
        }

        private void handleTransacaoLer(Map<String, Object> request, Map<String, Object> responseMap) {
            try {
                String token = (String) request.get("token");
                String dataInicial = (String) request.get("data_inicial");
                String dataFinal = (String) request.get("data_final");

                Usuario user = servidor.sessaoController.getUsuarioPorToken(token);
                if (user == null) {
                    responseMap.put("status", false);
                    responseMap.put("info", "Sessão inválida.");
                    return;
                }
                
                List<Map<String, Object>> transacoes = servidor.transacaoController.buscarExtrato(user.getId(), dataInicial, dataFinal);

                responseMap.put("status", true);
                responseMap.put("info", "Extrato recuperado com sucesso.");
                responseMap.put("transacoes", transacoes);

            } catch (Exception e) {
                responseMap.put("status", false);
                responseMap.put("info", "Erro ao buscar extrato: " + e.getMessage());
            }
        }
    }

    private static String getTimestamp() {
        return "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "]";
    }
}