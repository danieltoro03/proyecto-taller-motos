package com.tallermotosv1.controladores;

import com.tallermotosv1.dao.CotizacionDAO;
import com.tallermotosv1.dao.NotificacionDAO;
import com.tallermotosv1.utilidades.NotificadorEmail;

import java.math.BigDecimal;
import java.util.List;

public class CotizacionControlador {

    private final CotizacionDAO cotizacionDAO = new CotizacionDAO();
    private final NotificacionDAO notificacionDAO = new NotificacionDAO();

    public boolean generarYEnviarCotizacion(long idCita, long idMecanico, String correoCliente,
                                           String diagnostico, List<CotizacionDAO.Item> items) {
        try {
            long idCotizacion = cotizacionDAO.registrarCotizacion(idCita, idMecanico, diagnostico, items);

            if (idCotizacion > 0) {
                BigDecimal total = items.stream().map(CotizacionDAO.Item::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
                StringBuilder sb = new StringBuilder();
                sb.append("Estimado cliente,\n\n");
                sb.append("Se ha generado la cotización para el mantenimiento de su motocicleta.\n\n");
                sb.append("Diagnóstico Técnico: ").append(diagnostico).append("\n\n");
                sb.append("Detalles del Servicio:\n");
                for (CotizacionDAO.Item item : items) {
                    sb.append("- ").append(item.descripcion())
                      .append(" | Cant: ").append(item.cantidad())
                      .append(" | Subtotal: $").append(item.subtotal()).append("\n");
                }
                sb.append("\nTOTAL A PAGAR: $").append(total).append("\n\n");
                sb.append("Atentamente,\nEquipo Técnico del Taller.");

                // Llamada directa al método estático sin usar "new"
                NotificadorEmail.enviarCorreoAsync(
                        correoCliente,
                        "Cotización de Mantenimiento - Taller de Motos",
                        sb.toString(),
                        () -> {
                            try {
                                notificacionDAO.registrar("COTIZACION", true, idCita);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        },
                        motivoError -> {
                            try {
                                notificacionDAO.registrar("COTIZACION", false, idCita);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}