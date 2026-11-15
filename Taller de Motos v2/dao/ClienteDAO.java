package com.tallermotosv1.dao;

import com.tallermotosv1.dto.ClienteDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClienteDAO {

    /**
     * Busca el cliente por documento; si existe actualiza celular y correo,
     * si no existe lo crea. Devuelve su id. Usa la conexión recibida (transacción del llamador).
     */
    public long obtenerOCrear(Connection conn, ClienteDatos cliente) throws SQLException {
        String sqlBuscar = "SELECT id_cliente FROM Cliente WHERE documento_identidad = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlBuscar)) {
            ps.setString(1, cliente.documento());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id_cliente");
                    actualizarContacto(conn, id, cliente);
                    return id;
                }
            }
        }

        String sqlInsert = "INSERT INTO Cliente (documento_identidad, nombre_completo, telefono, correo_electronico) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.documento());
            ps.setString(2, cliente.nombre());
            ps.setString(3, cliente.celular());
            ps.setString(4, cliente.correo());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id del cliente creado.");
    }

    private void actualizarContacto(Connection conn, long idCliente, ClienteDatos cliente) throws SQLException {
        String sql = "UPDATE Cliente SET telefono = ?, correo_electronico = ? WHERE id_cliente = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.celular());
            ps.setString(2, cliente.correo());
            ps.setLong(3, idCliente);
            ps.executeUpdate();
        }
    }
}
