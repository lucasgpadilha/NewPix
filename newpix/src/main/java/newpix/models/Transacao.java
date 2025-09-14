package newpix.models;

import java.sql.Timestamp;

public class Transacao {
    private int id;
    private int idRemetente;
    private int idDestinatario;
    private double valor;
    private Timestamp data;

    public Transacao() {}

    public Transacao(int id, int idRemetente, int idDestinatario, double valor, Timestamp data) {
        this.id = id;
        this.idRemetente = idRemetente;
        this.idDestinatario = idDestinatario;
        this.valor = valor;
        this.data = data;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdRemetente() { return idRemetente; }
    public void setIdRemetente(int idRemetente) { this.idRemetente = idRemetente; }
    public int getIdDestinatario() { return idDestinatario; }
    public void setIdDestinatario(int idDestinatario) { this.idDestinatario = idDestinatario; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public Timestamp getData() { return data; }
    public void setData(Timestamp data) { this.data = data; }
}