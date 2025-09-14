package newpix.controllers;

import newpix.dao.SessaoDAO;
import newpix.models.Usuario;
import java.sql.SQLException;

public class SessaoController {
    private final SessaoDAO sessaoDAO = new SessaoDAO();

    public String criarSessao(int usuarioId) throws SQLException {
        return sessaoDAO.criarSessao(usuarioId);
    }

    public Usuario getUsuarioPorToken(String token) throws SQLException {
        return sessaoDAO.getUsuarioPorToken(token);
    }

    public void deletarSessao(String token) throws SQLException {
        sessaoDAO.deletarSessao(token);
    }
}