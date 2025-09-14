package newpix.controllers;

import newpix.dao.UsuarioDAO;
import newpix.models.Usuario;

import java.sql.SQLException;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public void cadastrarUsuario(Usuario usuario) throws SQLException {
        usuarioDAO.cadastrar(usuario);
    }

    public Usuario login(String cpf, String senha) throws SQLException {
        return usuarioDAO.login(cpf, senha);
    }

    public Usuario getUsuarioPorCpf(String cpf) throws SQLException {
        return usuarioDAO.getPorCpf(cpf);
    }
}