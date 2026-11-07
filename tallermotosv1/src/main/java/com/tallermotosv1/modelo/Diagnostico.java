package com.tallermotosv1.modelo;

import java.time.LocalDateTime;

public class Diagnostico {
    private int idDiagnostico;
    private String descripcionFalla;
    private LocalDateTime fechaDiagnostico;
    private Cita cita;
    private Usuario mecanico;

    public Diagnostico(int idDiagnostico, String descripcionFalla, LocalDateTime fechaDiagnostico, Cita cita, Usuario mecanico) {
        this.idDiagnostico = idDiagnostico;
        this.descripcionFalla = descripcionFalla;
        this.fechaDiagnostico = fechaDiagnostico;
        this.cita = cita;
        this.mecanico = mecanico;
    }

    public int getIdDiagnostico() {
        return idDiagnostico;
    }

    public void setIdDiagnostico(int idDiagnostico) {
        this.idDiagnostico = idDiagnostico;
    }

    public String getDescripcionFalla() {
        return descripcionFalla;
    }

    public void setDescripcionFalla(String descripcionFalla) {
        this.descripcionFalla = descripcionFalla;
    }

    public LocalDateTime getFechaDiagnostico() {
        return fechaDiagnostico;
    }

    public void setFechaDiagnostico(LocalDateTime fechaDiagnostico) {
        this.fechaDiagnostico = fechaDiagnostico;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public Usuario getMecanico() {
        return mecanico;
    }

    public void setMecanico(Usuario mecanico) {
        this.mecanico = mecanico;
    }
}