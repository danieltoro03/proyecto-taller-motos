package com.tallermotosv1.vistas;

import com.tallermotosv1.datos.RepositorioDatos;
import com.tallermotosv1.enums.EstadoCita;
import com.tallermotosv1.enums.RolUsuario;
import com.tallermotosv1.modelo.Cita;
import com.tallermotosv1.modelo.Usuario;
import com.tallermotosv1.utilidades.NotificadorEmail;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardFrame extends JFrame {
    private JTable tablaCitas;
    private DefaultTableModel modeloTabla;
    private Usuario usuarioLogueado;

    public DashboardFrame(Usuario usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        setTitle("Taller Motos - Panel de Control");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. Menú Lateral (Sidebar)
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(33, 37, 41)); // Color oscuro
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel lblLogo = new JLabel("  TALLER MOTOS  ");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        sidebar.add(lblLogo);

        JButton btnCitas = crearBotonSidebar("Ver Citas");
        JButton btnNuevaCita = crearBotonSidebar("Agendar Cita");
        JButton btnSalir = crearBotonSidebar("Cerrar Sesión");

        // Lógica del botón Agendar
        btnNuevaCita.addActionListener(e -> {
            new AgendarCitaFrame().setVisible(true);
        });
        
        // Ocultar botón de agendar si es mecánico
        if (usuarioLogueado.getRol() == RolUsuario.MECANICO) {
            btnNuevaCita.setVisible(false);
        }

        btnSalir.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });

        sidebar.add(btnCitas);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnNuevaCita);
        sidebar.add(Box.createVerticalGlue()); // Empuja el botón salir al fondo
        sidebar.add(btnSalir);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. Panel Principal (Donde va la tabla)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("Gestión de Citas y Reparaciones");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        mainPanel.add(lblTitulo, BorderLayout.NORTH);

        // Tabla de Citas
        String[] columnas = {"ID", "Cliente", "Placa", "Fecha", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Bloquear edición
        };
        tablaCitas = new JTable(modeloTabla);
        tablaCitas.setRowHeight(25);
        tablaCitas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(tablaCitas);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel de acciones (Botones inferiores)
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnActualizar = new JButton("Actualizar Tabla");
        JButton btnCambiarEstado = new JButton("Cambiar Estado");
        btnCambiarEstado.setBackground(new Color(0, 123, 255));
        btnCambiarEstado.setForeground(Color.WHITE);
        btnCambiarEstado.setFont(new Font("Segoe UI", Font.BOLD, 12));

        panelAcciones.add(btnActualizar);
        panelAcciones.add(btnCambiarEstado);
        mainPanel.add(panelAcciones, BorderLayout.SOUTH);

        // Eventos
        btnActualizar.addActionListener(e -> cargarCitas());
        btnCambiarEstado.addActionListener(e -> cambiarEstadoCita());

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Cargar datos iniciales
        cargarCitas();
    }

    private JButton crearBotonSidebar(String texto) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(new Color(33, 37, 41));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void cargarCitas() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        for (Cita c : RepositorioDatos.citas) {
            String nombreCliente = c.getMoto().getCliente().getNombre();
            String placa = c.getMoto().getPlaca();
            String fecha = c.getFechaHora().toString();
            String estado = c.getEstado().toString();

            modeloTabla.addRow(new Object[]{c.getIdCita(), nombreCliente, placa, fecha, estado});
        }
    }

        private void cambiarEstadoCita() {
        int filaSeleccionada = tablaCitas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una cita de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idCita = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
        Cita cita = RepositorioDatos.citas.stream().filter(c -> c.getIdCita() == idCita).findFirst().orElse(null);

        if (cita != null) {
            // Diálogo para elegir el nuevo estado
            String[] estados = {"PENDIENTE", "CONFIRMADA", "EN_PROCESO", "CANCELADA", "FINALIZADA"};
            String seleccion = (String) JOptionPane.showInputDialog(this, 
                    "Seleccione el nuevo estado para la cita de " + cita.getMoto().getCliente().getNombre() + ":",
                    "Cambiar Estado",
                    JOptionPane.QUESTION_MESSAGE, null, estados, cita.getEstado().toString());

            if (seleccion != null && !seleccion.equals(cita.getEstado().toString())) {
                // 1. Guardamos el estado anterior y actualizamos al nuevo
                EstadoCita estadoAnterior = cita.getEstado();
                cita.setEstado(EstadoCita.valueOf(seleccion));
                
                // 2. Refrescamos la tabla
                cargarCitas(); 
                
                // 3. Mostramos mensaje de éxito
                JOptionPane.showMessageDialog(this, "Estado actualizado a: " + seleccion);
                
                // 4. ENVIAR EL CORREO AL CLIENTE (Requerimiento RF04)
                try {
                    String asunto = "Actualización de Estado - Taller de Motos";
                    String mensaje = "Hola " + cita.getMoto().getCliente().getNombre() + ",\n\n"
                            + "Te informamos que el estado de reparación de tu motocicleta de placa " 
                            + cita.getMoto().getPlaca() + " ha cambiado.\n\n"
                            + "Estado anterior: " + estadoAnterior + "\n"
                            + "Nuevo estado: " + seleccion + "\n\n"
                            + "Puedes comunicarte con nosotros si tienes alguna duda.\n"
                            + "¡Gracias por confiar en el Taller de Motos!";

                    // Enviamos el correo al cliente
                    NotificadorEmail.enviarCorreo(cita.getMoto().getCliente().getCorreo(), asunto, mensaje);
                    
                    // (Opcional) Aquí también podrías guardar el registro en HistorialEstado
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Estado cambiado, pero hubo un error al enviar el correo: " + ex.getMessage(), "Error de Notificación", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        }
    }