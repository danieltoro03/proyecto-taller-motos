package com.tallermotosv1.dao;

import com.tallermotosv1.conexion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Registra en la tabla Notificacion cada intento de correo asociado a una cita. */
public class NotificacionDAO {
    public void registrar(String tipo, boolean enviado, long idCita) throws SQLException {
        String sql = "INSERT INTO Notificacion (tipo_notificacion, fecha_envio, estado_envio, id_cita) VALUES (?, NOW(), ?, ?)";
        try (Connection cn = ConexionDB.obtenerConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setString(2, enviado ? "ENVIADO" : "FALLIDO");
            ps.setLong(3, idCita);
            ps.executeUpdate();
        }
    }
}
