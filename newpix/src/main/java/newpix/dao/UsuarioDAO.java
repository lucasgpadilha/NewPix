package newpix.dao;

import newpix.configs.DatabaseConfig;
import newpix.models.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

	public void cadastrar(Usuario usuario) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConfig.getConnection();
            
            String sqlCheck = "SELECT id, ativo FROM usuarios WHERE cpf = ?";
            stmt = conn.prepareStatement(sqlCheck);
            stmt.setString(1, usuario.getCpf());
            rs = stmt.executeQuery();

            if (rs.next()) {

                boolean ativo = rs.getBoolean("ativo");
                int idExistente = rs.getInt("id");

                if (ativo) {

                    throw new SQLException("CPF já cadastrado.");
                } else {

                    rs.close();
                    stmt.close();

                    String sqlReativar = "UPDATE usuarios SET nome = ?, senha = ?, saldo = ?, ativo = 1 WHERE id = ?";
                    stmt = conn.prepareStatement(sqlReativar);
                    stmt.setString(1, usuario.getNome());
                    stmt.setString(2, usuario.getSenha());
                    stmt.setDouble(3, 0.0); 
                    stmt.setInt(4, idExistente);
                    stmt.executeUpdate();
                    return; 
                }
            }
            
 
            if(rs != null) rs.close();
            if(stmt != null) stmt.close();

            String sqlInsert = "INSERT INTO usuarios (nome, cpf, senha, saldo, ativo) VALUES (?, ?, ?, ?, 1)";
            stmt = conn.prepareStatement(sqlInsert);
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getSenha());
            stmt.setDouble(4, usuario.getSaldo());
            stmt.executeUpdate();

        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }
    public Usuario login(String cpf, String senha) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE cpf = ? AND senha = ? AND ativo = 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.setString(2, senha);
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


    public Usuario getPorCpf(String cpf) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE cpf = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
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

    public Usuario getPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
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

    public void atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ?, senha = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getSenha());
            stmt.setInt(3, usuario.getId());
            stmt.executeUpdate();
        }
    }

    public void deletar(int id) throws SQLException {

        String sql = "UPDATE usuarios SET ativo = 0 WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void atualizarSaldo(int usuarioId, double valor) throws SQLException {
        String sql = "UPDATE usuarios SET saldo = saldo + ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, valor);
            stmt.setInt(2, usuarioId);
            stmt.executeUpdate();
        }
    }
}