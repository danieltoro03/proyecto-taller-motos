package com.tallermotosv1.dto;

import java.time.LocalDateTime;

/** Fila de la tabla del Dashboard (cita + cliente + moto). */
public record CitaResumen(long idCita, String cliente, String placa,
                          LocalDateTime fechaHora, String estado, String correo) {
}
