package com.tallermotosv1.modelo;

import com.tallermotosv1.enums.EstadoCita;
import java.time.LocalDateTime;

public class Cita {
    private int idCita;
    private LocalDateTime fechaHora;
    private EstadoCita estado;
    private String motivo;
    private Moto moto;
    private Usuario recepcionista;

    public Cita(int idCita, LocalDateTime fechaHora, EstadoCita estado, String motivo, Moto moto, Usuario recepcionista) {
        this.idCita = idCita;
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.motivo = motivo;
        this.moto = moto;
        this.recepcionista = recepcionista;
    }

    public void confirmar() {
        this.estado = EstadoCita.CONFIRMADA;
    }

    public void cancelar() {
        this.estado = EstadoCita.CANCELADA;
    }

    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Moto getMoto() {
        return moto;
    }

    public void setMoto(Moto moto) {
        this.moto = moto;
    }

    public Usuario getRecepcionista() {
        return recepcionista;
    }

    public void setRecepcionista(Usuario recepcionista) {
        this.recepcionista = recepcionista;
    }
}