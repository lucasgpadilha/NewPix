package newpix.controllers;

import newpix.dao.TransacaoDAO;
import newpix.dao.UsuarioDAO;
import newpix.models.Usuario;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class TransacaoController {
    private final TransacaoDAO transacaoDAO = new TransacaoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void realizarTransferencia(int idRemetente, String cpfDestino, double valor) throws SQLException {
        if (valor <= 0) {
            throw new SQLException("O valor da transferência deve ser positivo.");
        }

        Usuario remetente = usuarioDAO.getPorId(idRemetente);
        Usuario destinatario = usuarioDAO.getPorCpf(cpfDestino);

        if (remetente == null) {
            throw new SQLException("Usuário remetente não encontrado.");
        }
        if (destinatario == null) {
            throw new SQLException("CPF de destino não encontrado.");
        }
        if (remetente.getId() == destinatario.getId()) {
            throw new SQLException("Você não pode enviar um PIX para si mesmo.");
        }
        if (remetente.getSaldo() < valor) {
            throw new SQLException("Saldo insuficiente para realizar a transferência.");
        }

        // A transação é feita de forma atômica no DAO
        transacaoDAO.criarTransacao(remetente, destinatario, valor);
    }

    public List<Map<String, Object>> buscarExtrato(int usuarioId, String dataInicialStr, String dataFinalStr) throws Exception {
        // CORREÇÃO AQUI: Usar ZonedDateTime para interpretar a data/hora completa com fuso horário (Z = UTC)
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        try {
            ZonedDateTime dataInicial = ZonedDateTime.parse(dataInicialStr, formatter);
            ZonedDateTime dataFinal = ZonedDateTime.parse(dataFinalStr, formatter);

            long dias = ChronoUnit.DAYS.between(dataInicial, dataFinal);

            if (dias < 0) {
                throw new Exception("A data inicial não pode ser posterior à data final.");
            }
            if (dias > 31) {
                throw new Exception("O período de pesquisa do extrato não pode exceder 31 dias.");
            }

            return transacaoDAO.getExtratoPorPeriodo(usuarioId, dataInicialStr, dataFinalStr);
        } catch (DateTimeParseException e) {
            throw new Exception("Formato de data inválido. Use yyyy-MM-dd'T'HH:mm:ss'Z'. Detalhe: " + e.getMessage());
        }
    }
}