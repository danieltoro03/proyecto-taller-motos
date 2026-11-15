package com.tallermotosv1.utilidades;

import java.util.regex.Pattern;

public final class Validador {
    private static final Pattern CORREO = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern DOCUMENTO = Pattern.compile("^[0-9]{5,15}$");
    private static final Pattern TELEFONO = Pattern.compile("^[0-9]{7,15}$");
    // Placas colombianas: ABC123 (carro) o ABC12D (moto)
    private static final Pattern PLACA = Pattern.compile("^[A-Z]{3}[0-9]{2}[A-Z0-9]$");

    private Validador() {
    }

    public static boolean esCorreoValido(String s) {
        return s != null && CORREO.matcher(s).matches();
    }

    public static boolean esDocumentoValido(String s) {
        return s != null && DOCUMENTO.matcher(s).matches();
    }

    public static boolean esTelefonoValido(String s) {
        return s != null && TELEFONO.matcher(s).matches();
    }

    public static boolean esPlacaValida(String s) {
        return s != null && PLACA.matcher(s).matches();
    }
}
