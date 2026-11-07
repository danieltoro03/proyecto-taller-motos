package com.tallermotosv1.modelo;

import com.tallermotosv1.enums.RolUsuario;

public class Usuario {
    private String correo;
    private String contrasena;
    private RolUsuario rol;

    public Usuario(String correo, String contrasena, RolUsuario rol) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
}