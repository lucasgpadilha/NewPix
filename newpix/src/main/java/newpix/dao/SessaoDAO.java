package newpix.dao;

import newpix.configs.DatabaseConfig;
import newpix.models.Usuario;
import java.sql.*;
import java.util.UUID;

public class SessaoDAO {

    public String criarSessao(int usuarioId) throws SQLException {
        String token = UUID.randomUUID().toString();
        String sql = "INSERT INTO sessoes (token, usuario_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();
            return token;
        }
    }

    public Usuario getUsuarioPorToken(String token) throws SQLException {
        String sql = "SELECT u.* FROM usuarios u JOIN sessoes s ON u.id = s.usuario_id WHERE s.token = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("cpf"),
                            rs.getString("senha"),
                            rs.getDouble("saldo")
                    );
                }
            }
        }
        return null;
    }

    public void deletarSessao(String token) throws SQLException {
        String sql = "DELETE FROM sessoes WHERE token = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        }
    }
    
    public void deletarSessoesPorUsuario(int usuarioId) throws SQLException {
        String sql = "DELETE FROM sessoes WHERE usuario_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();
        }
    }
}