package com.tallermotosv1.dao;

import com.tallermotosv1.excepciones.ReglaNegocioException;
import com.tallermotosv1.dto.MotoDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MotoDAO {

    /**
     * Busca la moto por placa. Si existe y es del mismo cliente la reutiliza;
     * si es de otro cliente lanza ReglaNegocioException; si no existe la crea.
     */
    public long obtenerOCrear(Connection conn, MotoDatos moto, long idCliente) throws SQLException, ReglaNegocioException {
        String sqlBuscar = "SELECT id_moto, id_cliente FROM Moto WHERE placa = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlBuscar)) {
            ps.setString(1, moto.placa());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (rs.getLong("id_cliente") != idCliente) {
                        throw new ReglaNegocioException("La placa " + moto.placa()
                                + " ya está registrada a nombre de otro cliente.");
                    }
                    return rs.getLong("id_moto");
                }
            }
        }

        String sqlInsert = "INSERT INTO Moto (placa, marca, modelo, cilindraje, tipo, id_cliente) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, moto.placa());
            ps.setString(2, moto.marca());
            ps.setString(3, moto.modelo());
            ps.setInt(4, moto.cilindraje());
            ps.setString(5, moto.tipo());
            ps.setLong(6, idCliente);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id de la moto creada.");
    }
}
