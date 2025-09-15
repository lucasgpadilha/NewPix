package newpix.dao;

import newpix.configs.DatabaseConfig;
import newpix.models.Usuario;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class TransacaoDAO {

    public void criarTransacao(Usuario remetente, Usuario destinatario, double valor) throws SQLException {
        String sqlUpdateRemetente = "UPDATE usuarios SET saldo = saldo - ? WHERE id = ?";
        String sqlUpdateDestinatario = "UPDATE usuarios SET saldo = saldo + ? WHERE id = ?";
        String sqlInsertTransacao = "INSERT INTO transacoes (id_remetente, id_destinatario, valor) VALUES (?, ?, ?)";
        Connection conn = null;

        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); 

            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateRemetente)) {
                stmt.setDouble(1, valor);
                stmt.setInt(2, remetente.getId());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateDestinatario)) {
                stmt.setDouble(1, valor);
                stmt.setInt(2, destinatario.getId());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlInsertTransacao)) {
                stmt.setInt(1, remetente.getId());
                stmt.setInt(2, destinatario.getId());
                stmt.setDouble(3, valor);
                stmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Map<String, Object>> getExtratoPorPeriodo(int usuarioId, String dataInicial, String dataFinal) throws SQLException {
        List<Map<String, Object>> extrato = new ArrayList<>();
        // CORREÇÃO: Query para buscar todos os dados necessários para o formato do protocolo
        String sql = "SELECT t.id, t.valor, t.data, " +
                     "rem.nome as remetente_nome, rem.cpf as remetente_cpf, " +
                     "dest.nome as destinatario_nome, dest.cpf as destinatario_cpf, " +
                     "t.id_remetente " +
                     "FROM transacoes t " +
                     "JOIN usuarios rem ON t.id_remetente = rem.id " +
                     "JOIN usuarios dest ON t.id_destinatario = dest.id " +
                     "WHERE (t.id_remetente = ? OR t.id_destinatario = ?) AND t.data BETWEEN ? AND ? " +
                     "ORDER BY t.data DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, usuarioId);
            stmt.setString(3, dataInicial);
            stmt.setString(4, dataFinal);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // CORREÇÃO: Montagem do mapa no formato exato do protocolo
                    Map<String, Object> transacao = new HashMap<>();
                    transacao.put("id", rs.getInt("id"));
                    transacao.put("valor_enviado", rs.getDouble("valor"));

                    Map<String, String> enviador = new HashMap<>();
                    enviador.put("nome", rs.getString("remetente_nome"));
                    enviador.put("cpf", rs.getString("remetente_cpf"));
                    transacao.put("usuario_enviador", enviador);

                    Map<String, String> recebedor = new HashMap<>();
                    recebedor.put("nome", rs.getString("destinatario_nome"));
                    recebedor.put("cpf", rs.getString("destinatario_cpf"));
                    transacao.put("usuario_recebedor", recebedor);
                    
                    Timestamp timestamp = rs.getTimestamp("data");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String dataFormatada = sdf.format(timestamp);

                    transacao.put("criado_em", dataFormatada);
                    transacao.put("atualizado_em", dataFormatada);
                    
                    // Adiciona um campo extra para facilitar a vida do front-end
                    transacao.put("tipo", rs.getInt("id_remetente") == usuarioId ? "enviada" : "recebida");

                    extrato.add(transacao);
                }
            }
        }
        return extrato;
    }
}