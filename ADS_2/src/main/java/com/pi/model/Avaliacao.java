package com.pi.model;

import java.time.LocalDateTime;

public class Avaliacao {

    private int id;
    private int idUsuario;
    private int idPrestador;
    private int nota;
    private String comentario;
    private LocalDateTime dataAvaliacao;

    public Avaliacao(int id, int idUsuario, int idPrestador, int nota,
                     String comentario, LocalDateTime dataAvaliacao) {
        setId(id);
        setIdUsuario(idUsuario);
        setIdPrestador(idPrestador);
        setNota(nota);
        setComentario(comentario);
        setDataAvaliacao(dataAvaliacao);
    }

    public Avaliacao(int idUsuario, int idPrestador, int nota,
                     String comentario, LocalDateTime dataAvaliacao) {
        this(-1, idUsuario, idPrestador, nota, comentario, dataAvaliacao);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdPrestador() {
        return idPrestador;
    }

    public void setIdPrestador(int idPrestador) {
        this.idPrestador = idPrestador;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        if (nota <= 5 && nota >= 1) {
            this.nota = nota;
        } else {
            throw new IllegalArgumentException("Nota inválida. ");
        }
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(LocalDateTime dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }
}
