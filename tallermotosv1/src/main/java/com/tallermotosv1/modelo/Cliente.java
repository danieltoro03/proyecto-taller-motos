package com.tallermotosv1.modelo;

public class Cliente {
    private int idCliente;
    private String documento;
    private String nombre;
    private String celular;
    private String correo;

    public Cliente(int idCliente, String documento, String nombre, String celular, String correo) {
        this.idCliente = idCliente;
        this.documento = documento;
        this.nombre = nombre;
        this.celular = celular;
        this.correo = correo;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}