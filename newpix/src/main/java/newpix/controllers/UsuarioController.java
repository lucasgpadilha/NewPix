package newpix.controllers;

import newpix.dao.UsuarioDAO;
import newpix.dao.TransacaoDAO; 
import newpix.models.Usuario;
import java.math.BigDecimal;
import java.sql.SQLException;

public class UsuarioController {

    private final UsuarioDAO usuarioDAO;
    private final TransacaoDAO transacaoDAO; 

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
        this.transacaoDAO = new TransacaoDAO(); 
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

    public void atualizarUsuario(Usuario usuario) throws SQLException {
        usuarioDAO.atualizar(usuario);
    }

    public void deletarUsuario(int id) throws SQLException {
        usuarioDAO.deletar(id);
    }


    public void depositar(Usuario usuario, double valor) throws SQLException {
        if (BigDecimal.valueOf(valor).scale() > 2) {
            throw new SQLException("O valor do depósito não pode ter mais que duas casas decimais.");
        }

        if (valor <= 0) {
            throw new SQLException("Valor de depósito deve ser positivo.");
        }
        

        transacaoDAO.criarDeposito(usuario, valor);
    }
}