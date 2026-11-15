package com.tallermotosv1.excepciones;

/** Error de una regla del negocio (se muestra tal cual al usuario). */
public class ReglaNegocioException extends Exception {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
