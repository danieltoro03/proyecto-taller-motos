package com.tallermotosv1.dao;

import com.tallermotosv1.conexion.ConexionDB;
import com.tallermotosv1.dto.CitaResumen;
import com.tallermotosv1.excepciones.ReglaNegocioException;
import com.tallermotosv1.dto.ClienteDatos;
import com.tallermotosv1.dto.MotoDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    public static final List<String> ESTADOS_DISPONIBLES = List.of(
            "PENDIENTE", "CONFIRMADA", "EN_PROCESO", "FINALIZADA", "CANCELADA");

    private static final int CAPACIDAD_MAXIMA = 6;
    private static final String ESTADOS_ACTIVOS = "('PENDIENTE', 'CONFIRMADA', 'EN_PROCESO')";

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final MotoDAO motoDAO = new MotoDAO();

    /**
     * RF01: valida cupo, crea/reutiliza cliente y moto, e inserta la cita
     * todo en UNA transacción. Devuelve el id de la cita creada.
     */
    public long registrarCitaCompleta(ClienteDatos cliente, MotoDatos moto, LocalDateTime fechaHora, String motivo)
            throws SQLException, ReglaNegocioException {
        return registrarCitaCompleta(cliente, moto, fechaHora, motivo, null);
    }

    public long registrarCitaCompleta(ClienteDatos cliente, MotoDatos moto, LocalDateTime fechaHora, String motivo, String correoRecepcionista)
            throws SQLException, ReglaNegocioException {
        try (Connection conn = ConexionDB.obtenerConexion()) {
            conn.setAutoCommit(false);
            boolean bloqueoAdquirido = false;
            try {
                bloqueoAdquirido = adquirirBloqueoCapacidad(conn);
                if (!bloqueoAdquirido) {
                    throw new ReglaNegocioException("No fue posible comprobar la capacidad del taller. Intente nuevamente.");
                }
                if (obtenerOcupacion(conn) >= CAPACIDAD_MAXIMA) {
                    throw new ReglaNegocioException("El taller alcanzó su capacidad máxima de "
                            + CAPACIDAD_MAXIMA + " motocicletas. Se liberará un espacio cuando una reparación sea finalizada o cancelada.");
                }
                long idCliente = clienteDAO.obtenerOCrear(conn, cliente);
                long idMoto = motoDAO.obtenerOCrear(conn, moto, idCliente);
                long idCita = insertarCita(conn, fechaHora, motivo, idMoto, correoRecepcionista);
                conn.commit();
                return idCita;
            } catch (SQLException | ReglaNegocioException | RuntimeException e) {
                conn.rollback();
                throw e;
            } finally {
                if (bloqueoAdquirido) {
                    liberarBloqueoCapacidad(conn);
                }
            }
        }
    }

    /** Cuenta todas las motocicletas que siguen ocupando un espacio del taller. */
    public int obtenerOcupacionActual() throws SQLException {
        try (Connection conn = ConexionDB.obtenerConexion()) {
            return obtenerOcupacion(conn);
        }
    }

    public int obtenerCapacidadMaxima() {
        return CAPACIDAD_MAXIMA;
    }

    private int obtenerOcupacion(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Cita WHERE estado IN " + ESTADOS_ACTIVOS;
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /* Serializa los registros para que dos recepcionistas no ocupen el último cupo a la vez. */
    private boolean adquirirBloqueoCapacidad(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT GET_LOCK('taller_motos_capacidad', 10)" );
             ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) == 1;
        }
    }

    private void liberarBloqueoCapacidad(Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT RELEASE_LOCK('taller_motos_capacidad')")) {
            ps.executeQuery().close();
        } catch (SQLException ex) {
            System.err.println("No se pudo liberar el bloqueo de capacidad: " + ex.getMessage());
        }
    }

    private long insertarCita(Connection conn, LocalDateTime fechaHora, String motivo, long idMoto, String correoRecepcionista) throws SQLException {
        String sql = "INSERT INTO Cita (fecha_hora, estado, motivo, id_moto, id_usuario_recepcionista) "
                + "VALUES (?, 'PENDIENTE', ?, ?, (SELECT id_usuario FROM Usuario WHERE correo = ?))";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(fechaHora));
            ps.setString(2, motivo);
            ps.setLong(3, idMoto);
            ps.setString(4, correoRecepcionista);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("No se pudo obtener el id de la cita creada.");
    }

    /** Lista para el Dashboard (más recientes primero). */
    public List<CitaResumen> listarResumen() throws SQLException {
        String sql = "SELECT c.id_cita, cl.nombre_completo, m.placa, c.fecha_hora, c.estado, cl.correo_electronico "
                + "FROM Cita c "
                + "JOIN Moto m ON c.id_moto = m.id_moto "
                + "JOIN Cliente cl ON m.id_cliente = cl.id_cliente "
                + "ORDER BY c.fecha_hora DESC";
        List<CitaResumen> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new CitaResumen(
                        rs.getLong("id_cita"),
                        rs.getString("nombre_completo"),
                        rs.getString("placa"),
                        rs.getTimestamp("fecha_hora").toLocalDateTime(),
                        rs.getString("estado"),
                        rs.getString("correo_electronico")));
            }
        }
        return lista;
    }

    /**
     * RF03: cambia el estado y guarda el historial en una transacción.
     * (El estado anterior se lee ANTES de actualizar; antes se guardaba el nuevo por error.)
     */
    public void actualizarEstadoCita(long idCita, String nuevoEstado, String observacion, String responsable)
            throws SQLException, ReglaNegocioException {
        try (Connection conn = ConexionDB.obtenerConexion()) {
            conn.setAutoCommit(false);
            try {
                String estadoAnterior;
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT estado FROM Cita WHERE id_cita = ? FOR UPDATE")) {
                    ps.setLong(1, idCita);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new ReglaNegocioException("La cita seleccionada ya no existe.");
                        }
                        estadoAnterior = rs.getString("estado");
                    }
                }

                if (!ESTADOS_DISPONIBLES.contains(nuevoEstado)) {
                    throw new ReglaNegocioException("El estado seleccionado no es válido.");
                }
                if ("FINALIZADA".equals(estadoAnterior) || "CANCELADA".equals(estadoAnterior)) {
                    throw new ReglaNegocioException("Una cita finalizada o cancelada no puede cambiar de estado.");
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO HistorialEstado (estado_anterior, estado_nuevo, fecha_hora_cambio, comentario_tecnico, id_cita, id_usuario_responsable) "
                                + "VALUES (?, ?, NOW(), ?, ?, (SELECT id_usuario FROM Usuario WHERE correo = ?))")) {
                    ps.setString(1, estadoAnterior);
                    ps.setString(2, nuevoEstado);
                    ps.setString(3, (observacion == null || observacion.isBlank()) ? null : observacion);
                    ps.setLong(4, idCita);
                    ps.setString(5, responsable); // correo del usuario; la BD guarda su id
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement("UPDATE Cita SET estado = ? WHERE id_cita = ?")) {
                    ps.setString(1, nuevoEstado);
                    ps.setLong(2, idCita);
                    ps.executeUpdate();
                }

                conn.commit();
            } catch (SQLException | ReglaNegocioException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
