package com.tallermotosv1.modelo;

import java.time.LocalDateTime;

public class HistorialEstado {
    private int idHistorial;
    private String estadoAnterior;
    private String estadoNuevo;
    private LocalDateTime fechaHoraCambio;
    private String comentarioTecnico;
    private Cita cita;
    private Usuario usuarioResponsable;

    public HistorialEstado(int idHistorial, String estadoAnterior, String estadoNuevo, String comentarioTecnico, Cita cita, Usuario usuarioResponsable) {
        this.idHistorial = idHistorial;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaHoraCambio = LocalDateTime.now();
        this.comentarioTecnico = comentarioTecnico;
        this.cita = cita;
        this.usuarioResponsable = usuarioResponsable;
    }

    public int getIdHistorial() {
        return idHistorial;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public LocalDateTime getFechaHoraCambio() {
        return fechaHoraCambio;
    }

    public String getComentarioTecnico() {
        return comentarioTecnico;
    }

    public Cita getCita() {
        return cita;
    }

    public Usuario getUsuarioResponsable() {
        return usuarioResponsable;
    }
}