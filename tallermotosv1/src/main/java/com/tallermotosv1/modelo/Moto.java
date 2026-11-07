package com.tallermotosv1.modelo;

public class Moto {
    private int idMoto;
    private String placa;
    private String marca;
    private String modelo;
    private int cilindraje;
    private String tipo;
    private Cliente cliente;

    public Moto(int idMoto, String placa, String marca, String modelo, int cilindraje, String tipo, Cliente cliente) {
        this.idMoto = idMoto;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.cilindraje = cilindraje;
        this.tipo = tipo;
        this.cliente = cliente;
    }

    public int getIdMoto() {
        return idMoto;
    }

    public void setIdMoto(int idMoto) {
        this.idMoto = idMoto;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getCilindraje() {
        return cilindraje;
    }

    public void setCilindraje(int cilindraje) {
        this.cilindraje = cilindraje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}