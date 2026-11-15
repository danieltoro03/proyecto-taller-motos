package com.tallermotosv1.conexion;

import com.tallermotosv1.utilidades.Configuracion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private ConexionDB() {
    }

    /**
     * Abre una conexión nueva. Lanza SQLException si falla (nunca devuelve null),
     * así quien la llama puede mostrar el error real al usuario.
     */
    public static Connection obtenerConexion() throws SQLException {
        String url = Configuracion.get("db.url");
        if (url == null || url.isEmpty()) {
            throw new SQLException("Falta db.url en config.properties.");
        }
        return DriverManager.getConnection(url, Configuracion.get("db.user"), Configuracion.get("db.password"));
    }
}
