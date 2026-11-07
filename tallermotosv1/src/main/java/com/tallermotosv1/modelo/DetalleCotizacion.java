package com.tallermotosv1.modelo;

import com.tallermotosv1.enums.TipoDetalle;
import java.math.BigDecimal;

public class DetalleCotizacion {
    private int idDetalle;
    private TipoDetalle tipoDetalle;
    private String descripcionItem;
    private int cantidad;
    private BigDecimal valorUnitario;
    private BigDecimal subtotal;
    private Cotizacion cotizacion;

    public DetalleCotizacion(int idDetalle, TipoDetalle tipoDetalle, String descripcionItem, int cantidad, BigDecimal valorUnitario, Cotizacion cotizacion) {
        this.idDetalle = idDetalle;
        this.tipoDetalle = tipoDetalle;
        this.descripcionItem = descripcionItem;
        this.cantidad = cantidad;
        this.valorUnitario = valorUnitario;
        this.cotizacion = cotizacion;
        calcularSubtotal();
    }

    public void calcularSubtotal() {
        this.subtotal = this.valorUnitario.multiply(new BigDecimal(this.cantidad));
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public TipoDetalle getTipoDetalle() {
        return tipoDetalle;
    }

    public void setTipoDetalle(TipoDetalle tipoDetalle) {
        this.tipoDetalle = tipoDetalle;
    }

    public String getDescripcionItem() {
        return descripcionItem;
    }

    public void setDescripcionItem(String descripcionItem) {
        this.descripcionItem = descripcionItem;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
        calcularSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public Cotizacion getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(Cotizacion cotizacion) {
        this.cotizacion = cotizacion;
    }
}