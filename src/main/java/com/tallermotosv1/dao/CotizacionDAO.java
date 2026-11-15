package com.tallermotosv1.dao;

import com.tallermotosv1.conexion.ConexionDB;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

/** Operaciones transaccionales de diagnóstico, cotización y sus conceptos. */
public class CotizacionDAO {

    public record Item(String tipo, String descripcion, int cantidad, BigDecimal valorUnitario) {
        public Item {
            if (!"MANO_DE_OBRA".equals(tipo) && !"REPUESTO".equals(tipo))
                throw new IllegalArgumentException("Tipo de detalle no válido.");
            if (descripcion == null || descripcion.isBlank())
                throw new IllegalArgumentException("La descripción del concepto es obligatoria.");
            if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
            if (valorUnitario == null || valorUnitario.signum() < 0)
                throw new IllegalArgumentException("El valor unitario no puede ser negativo.");
        }
        public BigDecimal subtotal() { return valorUnitario.multiply(BigDecimal.valueOf(cantidad)); }
    }

    /** Crea el diagnóstico, la cotización y todos sus detalles en una sola transacción. */
    public long registrarCotizacion(long idCita, long idMecanico, String descripcionDiagnostico,
                                    List<Item> items) throws SQLException {
        if (descripcionDiagnostico == null || descripcionDiagnostico.isBlank())
            throw new IllegalArgumentException("La descripción del diagnóstico es obligatoria.");
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("Agregue al menos un concepto a la cotización.");

        String sqlDiagnostico = "INSERT INTO Diagnostico (descripcion_falla, fecha_diagnostico, id_cita, id_usuario_mecanico) VALUES (?, NOW(), ?, ?)";
        String sqlCotizacion = "INSERT INTO Cotizacion (fecha_emision, estado, gran_total, id_diagnostico) VALUES (NOW(), 'BORRADOR', ?, ?)";
        String sqlDetalle = "INSERT INTO DetalleCotizacion (tipo_detalle, descripcion_item, cantidad, valor_unitario, subtotal, id_cotizacion) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cn = ConexionDB.obtenerConexion()) {
            cn.setAutoCommit(false);
            try {
                long idDiagnostico;
                try (PreparedStatement ps = cn.prepareStatement(sqlDiagnostico, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, descripcionDiagnostico.trim());
                    ps.setLong(2, idCita);
                    ps.setLong(3, idMecanico);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No se pudo crear el diagnóstico.");
                        idDiagnostico = keys.getLong(1);
                    }
                }

                BigDecimal total = items.stream().map(Item::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
                long idCotizacion;
                try (PreparedStatement ps = cn.prepareStatement(sqlCotizacion, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setBigDecimal(1, total);
                    ps.setLong(2, idDiagnostico);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new SQLException("No se pudo crear la cotización.");
                        idCotizacion = keys.getLong(1);
                    }
                }

                try (PreparedStatement ps = cn.prepareStatement(sqlDetalle)) {
                    for (Item item : items) {
                        ps.setString(1, item.tipo());
                        ps.setString(2, item.descripcion().trim());
                        ps.setInt(3, item.cantidad());
                        ps.setBigDecimal(4, item.valorUnitario());
                        ps.setBigDecimal(5, item.subtotal());
                        ps.setLong(6, idCotizacion);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                cn.commit();
                return idCotizacion;
            } catch (SQLException | RuntimeException ex) {
                cn.rollback();
                throw ex;
            }
        }
    }

    public void actualizarEstado(long idCotizacion, String estado) throws SQLException {
        if (!List.of("BORRADOR", "ENVIADA", "ACEPTADA", "RECHAZADA").contains(estado))
            throw new IllegalArgumentException("Estado de cotización no válido.");
        try (Connection cn = ConexionDB.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement("UPDATE Cotizacion SET estado = ? WHERE id_cotizacion = ?")) {
            ps.setString(1, estado);
            ps.setLong(2, idCotizacion);
            if (ps.executeUpdate() == 0) throw new SQLException("La cotización indicada no existe.");
        }
    }

    /** Mantiene compatibilidad con la firma utilizada en versiones anteriores. */
    public boolean registrarCotizacionConDetalles(String diagnostico, BigDecimal total, Long idCita,
            String tipoDetalle, String descripcion, int cantidad, BigDecimal valorUnitario) {
        try {
            long idMecanico;
            try (Connection cn = ConexionDB.obtenerConexion();
                 PreparedStatement ps = cn.prepareStatement("SELECT id_usuario FROM Usuario WHERE rol = 'MECANICO' ORDER BY id_usuario LIMIT 1");
                 ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("No existe un usuario con rol MECANICO.");
                idMecanico = rs.getLong(1);
            }
            Item item = new Item(tipoDetalle, descripcion, cantidad, valorUnitario);
            long idCotizacion = registrarCotizacion(idCita, idMecanico, diagnostico, List.of(item));
            return idCotizacion > 0;
        } catch (Exception ex) {
            System.err.println("Error al registrar cotización: " + ex.getMessage());
            return false;
        }
    }
}
