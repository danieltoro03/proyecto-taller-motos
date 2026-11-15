package com.tallermotosv1.controladores;

import com.tallermotosv1.dao.CitaDAO;
import com.tallermotosv1.dto.CitaResumen;
import com.tallermotosv1.dto.ClienteDatos;
import com.tallermotosv1.dto.MotoDatos;
import com.tallermotosv1.excepciones.ReglaNegocioException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class CitaControlador {
    private final CitaDAO citaDAO = new CitaDAO();

    /** Agenda la cita (y crea cliente/moto si no existen). Devuelve el id de la cita. */
    public long agendarCita(ClienteDatos cliente, MotoDatos moto, LocalDateTime fechaHora, String motivo)
            throws SQLException, ReglaNegocioException {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("La fecha y hora de la cita ya pasaron.");
        }
        return citaDAO.registrarCitaCompleta(cliente, moto, fechaHora, motivo);
    }

    public long agendarCita(ClienteDatos cliente, MotoDatos moto, LocalDateTime fechaHora, String motivo, String correoRecepcionista)
            throws SQLException, ReglaNegocioException {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new ReglaNegocioException("La fecha y hora de la cita ya pasaron.");
        }
        return citaDAO.registrarCitaCompleta(cliente, moto, fechaHora, motivo, correoRecepcionista);
    }

    public List<CitaResumen> listarCitas() throws SQLException {
        return citaDAO.listarResumen();
    }

    public void actualizarEstado(long idCita, String nuevoEstado, String observacion, String responsable)
            throws SQLException, ReglaNegocioException {
        citaDAO.actualizarEstadoCita(idCita, nuevoEstado, observacion, responsable);
    }
}
