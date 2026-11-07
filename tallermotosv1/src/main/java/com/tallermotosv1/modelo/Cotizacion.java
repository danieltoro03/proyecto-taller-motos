package com.tallermotosv1.modelo;

import com.tallermotosv1.enums.EstadoCotizacion;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Cotizacion {
    private int idCotizacion;
    private LocalDateTime fechaEmision;
    private EstadoCotizacion estado;
    private BigDecimal granTotal;
    private Diagnostico diagnostico;

    public Cotizacion(int idCotizacion, LocalDateTime fechaEmision, Diagnostico diagnostico) {
        this.idCotizacion = idCotizacion;
        this.fechaEmision = fechaEmision;
        this.estado = EstadoCotizacion.BORRADOR;
        this.granTotal = BigDecimal.ZERO;
        this.diagnostico = diagnostico;
    }

    public void aceptar() {
        this.estado = EstadoCotizacion.ACEPTADA;
    }

    public void rechazar() {
        this.estado = EstadoCotizacion.RECHAZADA;
    }

    public int getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(int idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public EstadoCotizacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoCotizacion estado) {
        this.estado = estado;
    }

    public BigDecimal getGranTotal() {
        return granTotal;
    }

    public void setGranTotal(BigDecimal granTotal) {
        this.granTotal = granTotal;
    }

    public Diagnostico getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(Diagnostico diagnostico) {
        this.diagnostico = diagnostico;
    }
}