package com.tallermotosv1.utilidades;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/** Lee config.properties (src/main/resources) una sola vez. */
public final class Configuracion {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = Configuracion.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                System.err.println("No se encontró config.properties en src/main/resources.");
            } else {
                PROPS.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            System.err.println("Error leyendo config.properties: " + e.getMessage());
        }
    }

    private Configuracion() {
    }

    public static String get(String clave) {
        String valor = PROPS.getProperty(clave);
        return valor == null ? null : valor.trim();
    }

    public static String get(String clave, String porDefecto) {
        String valor = get(clave);
        return (valor == null || valor.isEmpty()) ? porDefecto : valor;
    }
}
