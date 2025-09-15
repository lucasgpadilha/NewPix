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
        // CORREÇÃO AQUI: Nomes das colunas ajustados para id_remetente e id_destinatario
        String sqlInsertTransacao = "INSERT INTO transacoes (id_remetente, id_destinatario, valor) VALUES (?, ?, ?)";
        Connection conn = null;

        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Inicia a transação

            // Debita da conta do remetente
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateRemetente)) {
                stmt.setDouble(1, valor);
                stmt.setInt(2, remetente.getId());
                stmt.executeUpdate();
            }

            // Credita na conta do destinatário
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateDestinatario)) {
                stmt.setDouble(1, valor);
                stmt.setInt(2, destinatario.getId());
                stmt.executeUpdate();
            }

            // Registra a transação
            try (PreparedStatement stmt = conn.prepareStatement(sqlInsertTransacao)) {
                stmt.setInt(1, remetente.getId());
                stmt.setInt(2, destinatario.getId());
                stmt.setDouble(3, valor);
                stmt.executeUpdate();
            }

            conn.commit(); // Efetiva a transação

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Desfaz tudo em caso de erro
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            // Lançar a exceção original para o controller poder tratá-la
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
        // CORREÇÃO AQUI: Simplificado e adicionado o campo "tipo" e "outro_participante"
        String sql = "SELECT t.valor, t.data, " +
                     "CASE WHEN t.id_remetente = ? THEN 'enviada' ELSE 'recebida' END as tipo, " +
                     "CASE WHEN t.id_remetente = ? THEN dest.nome ELSE rem.nome END as outro_participante " +
                     "FROM transacoes t " +
                     "JOIN usuarios rem ON t.id_remetente = rem.id " +
                     "JOIN usuarios dest ON t.id_destinatario = dest.id " +
                     "WHERE (t.id_remetente = ? OR t.id_destinatario = ?) AND t.data BETWEEN ? AND ? " +
                     "ORDER BY t.data DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, usuarioId);
            stmt.setInt(3, usuarioId);
            stmt.setInt(4, usuarioId);
            stmt.setString(5, dataInicial);
            stmt.setString(6, dataFinal);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> transacao = new HashMap<>();
                    transacao.put("valor", rs.getDouble("valor"));
                    
                    Timestamp timestamp = rs.getTimestamp("data");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    transacao.put("data", sdf.format(timestamp));

                    transacao.put("tipo", rs.getString("tipo"));
                    transacao.put("outro_participante", rs.getString("outro_participante"));
                    extrato.add(transacao);
                }
            }
        }
        return extrato;
    }
}