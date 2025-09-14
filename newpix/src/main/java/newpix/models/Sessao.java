package newpix.models;

import java.sql.Timestamp;

public class Sessao {
    private String token;
    private int usuarioId;
    private Timestamp criadoEm;

    // Getters e Setters...
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public Timestamp getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Timestamp criadoEm) { this.criadoEm = criadoEm; }
}