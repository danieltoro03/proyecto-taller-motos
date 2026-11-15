package com.tallermotosv1.dto;

/** Datos de usuario mostrados en la tabla de administración. */
public record UsuarioResumen(int idUsuario, String nombre, String correo, String rol) {}
