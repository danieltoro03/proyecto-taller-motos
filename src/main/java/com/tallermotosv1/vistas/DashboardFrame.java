package com.tallermotosv1.vistas;

import com.tallermotosv1.controladores.CitaControlador;
import com.tallermotosv1.dao.CitaDAO;
import com.tallermotosv1.dto.CitaResumen;
import com.tallermotosv1.dto.UsuarioSesion;
import com.tallermotosv1.excepciones.ReglaNegocioException;

import com.tallermotosv1.controladores.UsuarioControlador;
import com.tallermotosv1.utilidades.NotificadorEmail;
import com.tallermotosv1.utilidades.TareaSegundoPlano;
import com.tallermotosv1.dao.NotificacionDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class DashboardFrame extends JFrame {
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a", Locale.ENGLISH);

    private JTable tablaCitas;
    private DefaultTableModel modeloTabla;
    private JButton btnActualizar;
    private JButton btnCambiarEstado;
    private final UsuarioSesion usuarioLogueado;
    private final CitaControlador citaControlador;
    private List<CitaResumen> citasActuales = new ArrayList<>();

    private JLabel lblTotal, lblPendientes, lblProceso, lblCapacidad;

    private JPanel panelCentral;
    private CardLayout cardLayout;
    private List<JButton> botonesSidebar = new ArrayList<>();

    public DashboardFrame(UsuarioSesion usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
        this.citaControlador = new CitaControlador();
        setTitle("Taller de Motos");
        setSize(1120, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(EstiloUI.FONDO);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setBackground(EstiloUI.OSCURO);
        sidebar.setPreferredSize(new Dimension(225, 0));
        sidebar.setBorder(new javax.swing.border.EmptyBorder(22, 15, 18, 15));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("Taller de Motos");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setBorder(new javax.swing.border.EmptyBorder(4, 8, 20, 0));
        sidebar.add(logo);

        JLabel seccion = new JLabel("OPERACIÓN");
        seccion.setForeground(new Color(130, 136, 146));
        seccion.setFont(new Font("Segoe UI", Font.BOLD, 10));
        seccion.setBorder(new javax.swing.border.EmptyBorder(0, 10, 10, 0));
        sidebar.add(seccion);

        // CardLayout central container
        cardLayout = new CardLayout();
        panelCentral = new JPanel(cardLayout);

        JPanel panelTablaCitas = crearPanelTablaCitas();
        AgendarCitaPanel panelAgendarCita = new AgendarCitaPanel(usuarioLogueado.correo(), () -> {
            cargarCitas();
            cardLayout.show(panelCentral, "CITAS");
        });
        UsuariosPanel panelUsuarios = new UsuariosPanel(usuarioLogueado);

        panelCentral.add(panelTablaCitas, "CITAS");
        panelCentral.add(panelAgendarCita, "AGENDAR");
        panelCentral.add(panelUsuarios, "USUARIOS");

        JButton btnCitas = crearBotonSidebar("Panel de citas", "CITAS");
        JButton btnNuevaCita = crearBotonSidebar("Agendar cita", "AGENDAR");
        JButton btnUsuarios = crearBotonSidebar("Usuarios", "USUARIOS");
        JButton btnMiClave = crearBotonSidebar("Mi contraseña", "CLAVE");
        JButton btnSalir = crearBotonSidebar("Cerrar sesión", "SALIR");

        if ("MECANICO".equalsIgnoreCase(usuarioLogueado.rol())) btnNuevaCita.setVisible(false);
        btnUsuarios.setVisible("ADMINISTRADOR".equalsIgnoreCase(usuarioLogueado.rol()));

        sidebar.add(btnCitas); sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(btnNuevaCita); sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(btnUsuarios); sidebar.add(Box.createVerticalGlue());

        JPanel usuarioBox = new JPanel(new BorderLayout(8, 3));
        usuarioBox.setBackground(new Color(30, 34, 42));
        usuarioBox.setBorder(new javax.swing.border.EmptyBorder(12, 10, 12, 8));
        JLabel inicial = new JLabel("" + usuarioLogueado.correo().substring(0, 1).toUpperCase(), SwingConstants.CENTER);
        inicial.setOpaque(true); inicial.setBackground(EstiloUI.NARANJA); inicial.setForeground(Color.WHITE);
        inicial.setFont(new Font("Segoe UI", Font.BOLD, 15)); inicial.setPreferredSize(new Dimension(34, 34));
        usuarioBox.add(inicial, BorderLayout.WEST);
        JLabel usuarioLabel = new JLabel("<html><b style='color:#FFFFFF'>" + usuarioLogueado.correo() + "</b><br><span style='color:#AEB4BE'>" + usuarioLogueado.rol() + "</span></html>");
        usuarioLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10)); usuarioBox.add(usuarioLabel, BorderLayout.CENTER);
        usuarioBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62)); sidebar.add(usuarioBox);
        sidebar.add(Box.createVerticalStrut(8)); sidebar.add(btnMiClave); sidebar.add(btnSalir);

        add(sidebar, BorderLayout.WEST);
        add(panelCentral, BorderLayout.CENTER);

        activarBoton(btnCitas);
        cargarCitas();
    }

    private JButton crearBotonSidebar(String texto, String actionKey) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Boolean activo = (Boolean) getClientProperty("activo");
                if (Boolean.TRUE.equals(activo)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fillOval(10, (getHeight() / 2) - 4, 8, 8);
                    g2.dispose();
                }
            }
        };

        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(170, 175, 185));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 26, 10, 8));

        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            if ("SALIR".equals(actionKey)) {
                new LoginFrame().setVisible(true);
                dispose();
            } else if ("CLAVE".equals(actionKey)) {
                cambiarMiContrasena();
            } else {
                activarBoton(btn);
                if ("CITAS".equals(actionKey)) cargarCitas();
                cardLayout.show(panelCentral, actionKey);
            }
        });

        botonesSidebar.add(btn);
        return btn;
    }

    private void activarBoton(JButton seleccionado) {
        for (JButton b : botonesSidebar) {
            b.putClientProperty("activo", false);
            b.setForeground(new Color(170, 175, 185));
            b.repaint();
        }
        seleccionado.putClientProperty("activo", true);
        seleccionado.setForeground(Color.WHITE);
        seleccionado.repaint();
    }

    private JPanel crearPanelTablaCitas() {
        JPanel main = new JPanel(new BorderLayout(0, 18));
        main.setBackground(EstiloUI.FONDO);
        main.setBorder(new javax.swing.border.EmptyBorder(28, 30, 25, 30));

        JPanel encabezado = new JPanel(new BorderLayout()); encabezado.setOpaque(false);
        JPanel titulos = new JPanel(); titulos.setOpaque(false); titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("Panel de citas"); titulo.setFont(EstiloUI.TITULO); titulo.setForeground(EstiloUI.TEXTO);
        JLabel subtitulo = new JLabel("Gestiona las citas y el estado de las reparaciones"); subtitulo.setFont(EstiloUI.NORMAL); subtitulo.setForeground(EstiloUI.SECUNDARIO);
        titulos.add(titulo); titulos.add(Box.createVerticalStrut(5)); titulos.add(subtitulo); encabezado.add(titulos, BorderLayout.WEST);
        main.add(encabezado, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 17)); centro.setOpaque(false);
        JPanel metricas = new JPanel(new GridLayout(1, 4, 14, 0)); metricas.setOpaque(false);
        lblTotal = crearValorMetrica("0", "Citas registradas");
        lblPendientes = crearValorMetrica("0", "Pendientes");
        lblProceso = crearValorMetrica("0", "En proceso");
        lblCapacidad = crearValorMetrica("—", "Capacidad del taller");
        metricas.add(tarjetaMetrica(lblTotal)); metricas.add(tarjetaMetrica(lblPendientes));
        metricas.add(tarjetaMetrica(lblProceso)); metricas.add(tarjetaMetrica(lblCapacidad));
        centro.add(metricas, BorderLayout.NORTH);

        JPanel tablaCard = EstiloUI.tarjeta(); tablaCard.setLayout(new BorderLayout(0, 12));
        JPanel tablaHeader = new JPanel(new BorderLayout()); tablaHeader.setOpaque(false);
        JLabel tituloTabla = new JLabel("Citas y reparaciones"); tituloTabla.setFont(EstiloUI.MEDIANA); tituloTabla.setForeground(EstiloUI.TEXTO);
        btnActualizar = EstiloUI.boton("Actualizar tabla", false);
        tablaHeader.add(tituloTabla, BorderLayout.WEST); tablaHeader.add(btnActualizar, BorderLayout.EAST); tablaCard.add(tablaHeader, BorderLayout.NORTH);
        String[] columnas = {"ID", "CLIENTE", "PLACA", "FECHA Y HORA", "ESTADO"};
        modeloTabla = new DefaultTableModel(columnas, 0) { public boolean isCellEditable(int row, int column) { return false; } };
        tablaCitas = new JTable(modeloTabla); tablaCitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        EstiloUI.estilizarTabla(tablaCitas);
        JScrollPane scroll = new JScrollPane(tablaCitas); scroll.setBorder(BorderFactory.createLineBorder(EstiloUI.BORDE));
        tablaCard.add(scroll, BorderLayout.CENTER);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); acciones.setOpaque(false);
        btnCambiarEstado = EstiloUI.boton("Cambiar estado", true); acciones.add(btnCambiarEstado); tablaCard.add(acciones, BorderLayout.SOUTH);
        centro.add(tablaCard, BorderLayout.CENTER);
        main.add(centro, BorderLayout.CENTER);

        btnActualizar.addActionListener(e -> cargarCitas());
        btnCambiarEstado.addActionListener(e -> cambiarEstadoCita());

        return main;
    }

    private JLabel crearValorMetrica(String valor, String texto) {
        return new JLabel("<html><div style='font-size:25px;font-weight:700;color:#202225'>" + valor + "</div><div style='font-size:11px;color:#777D85'>" + texto + "</div></html>");
    }

    private JPanel tarjetaMetrica(JLabel contenido) {
        JPanel p = EstiloUI.tarjeta(); p.setLayout(new BorderLayout()); p.add(contenido, BorderLayout.CENTER); return p;
    }

    private void cambiarMiContrasena() {
        JPasswordField txtActual = new JPasswordField(20);
        JPasswordField txtNueva = new JPasswordField(20);
        JPasswordField txtRepetir = new JPasswordField(20);
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 4));
        panel.add(new JLabel("Contraseña actual:"));
        panel.add(txtActual);
        panel.add(new JLabel("Contraseña nueva (mínimo " + UsuarioControlador.LARGO_MINIMO_CLAVE + " caracteres):"));
        panel.add(txtNueva);
        panel.add(new JLabel("Repetir contraseña nueva:"));
        panel.add(txtRepetir);

        while (true) {
            int op = JOptionPane.showConfirmDialog(this, panel, "Cambiar Mi Contraseña",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (op != JOptionPane.OK_OPTION) {
                return;
            }
            String actual = new String(txtActual.getPassword());
            String nueva = new String(txtNueva.getPassword());
            String error = !nueva.equals(new String(txtRepetir.getPassword()))
                    ? "Las contraseñas nuevas no coinciden."
                    : UsuarioControlador.validarContrasena(nueva);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Datos inválidos", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            TareaSegundoPlano.ejecutar(this, () -> {
                new UsuarioControlador().cambiarMiContrasena(usuarioLogueado.correo(), actual, nueva);
                return null;
            }, r -> JOptionPane.showMessageDialog(this, "Contraseña actualizada correctamente."));
            return;
        }
    }

    private void cargarCitas() {
        btnActualizar.setEnabled(false);
        new SwingWorker<List<CitaResumen>, Void>() {
            @Override
            protected List<CitaResumen> doInBackground() throws Exception {
                return citaControlador.listarCitas();
            }

            @Override
            protected void done() {
                btnActualizar.setEnabled(true);
                try {
                    citasActuales = get();
                    modeloTabla.setRowCount(0);
                    int pendientes = 0, proceso = 0;
                    for (CitaResumen c : citasActuales) {
                        if ("PENDIENTE".equalsIgnoreCase(c.estado()) || "CONFIRMADA".equalsIgnoreCase(c.estado())) pendientes++;
                        if ("EN_PROCESO".equalsIgnoreCase(c.estado()) || "EN PROCESO".equalsIgnoreCase(c.estado())) proceso++;
                    }
                    lblTotal.setText("<html><div style='font-size:25px;font-weight:700;color:#202225'>" + citasActuales.size() + "</div><div style='font-size:11px;color:#777D85'>Citas registradas</div></html>");
                    lblPendientes.setText("<html><div style='font-size:25px;font-weight:700;color:#202225'>" + pendientes + "</div><div style='font-size:11px;color:#777D85'>Pendientes</div></html>");
                    lblProceso.setText("<html><div style='font-size:25px;font-weight:700;color:#202225'>" + proceso + "</div><div style='font-size:11px;color:#777D85'>En proceso</div></html>");
                    int ocupacion = new CitaDAO().obtenerOcupacionActual();
                    int capacidad = new CitaDAO().obtenerCapacidadMaxima();
                    lblCapacidad.setText("<html><div style='font-size:25px;font-weight:700;color:#202225'>" + ocupacion + "/" + capacidad + "</div><div style='font-size:11px;color:#777D85'>Motocicletas en el taller</div></html>");
                    for (CitaResumen c : citasActuales) {
                        modeloTabla.addRow(new Object[]{
                                c.idCita(), c.cliente(), c.placa(), c.fechaHora().format(FORMATO_FECHA), c.estado()});
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    JOptionPane.showMessageDialog(DashboardFrame.this,
                            "Error al cargar las citas desde la base de datos: " + ex.getCause().getMessage(),
                            "Error de Conexión", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void cambiarEstadoCita() {
        int fila = tablaCitas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione una cita de la tabla.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        final CitaResumen cita = citasActuales.get(tablaCitas.convertRowIndexToModel(fila));

        JComboBox<String> cbxEstado = new JComboBox<>(CitaDAO.ESTADOS_DISPONIBLES.toArray(new String[0]));
        try {
            cbxEstado.setSelectedItem(cita.estado());
        } catch (IllegalArgumentException ignorado) {}
        
        JTextField txtObservacion = new JTextField(20);
        
        // Campos adicionales para el Diagnóstico y Cotización en el mismo diálogo de cambio de estado
        JTextField txtDiagnostico = new JTextField(20);
        JTextField txtItemDesc = new JTextField(20);
        JTextField txtCantidad = new JTextField("1", 5);
        JTextField txtValorUnitario = new JTextField("0.00", 10);
        JComboBox<String> cbxTipoDetalle = new JComboBox<>(new String[]{"MANO_DE_OBRA", "REPUESTO"});

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Nuevo estado:")); panel.add(cbxEstado);
        panel.add(new JLabel("Observación general:")); panel.add(txtObservacion);
        panel.add(new JSeparator()); panel.add(new JSeparator()); // Separador visual
        panel.add(new JLabel("<html><b>Diagnóstico de falla:</b></html>")); panel.add(txtDiagnostico);
        panel.add(new JLabel("Tipo de ítem cotización:")); panel.add(cbxTipoDetalle);
        panel.add(new JLabel("Descripción repuesto/servicio:")); panel.add(txtItemDesc);
        panel.add(new JLabel("Cantidad:")); panel.add(txtCantidad);
        panel.add(new JLabel("Valor Unitario ($):")); panel.add(txtValorUnitario);

        int opcion = JOptionPane.showConfirmDialog(this, panel, "Actualizar Estado y Cotización",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) return;

        final String nuevoEstado = (String) cbxEstado.getSelectedItem();
        final String observacion = txtObservacion.getText().trim();
        if (nuevoEstado == null) return;

        final String diagnostico = txtDiagnostico.getText().trim();
        final String descItem = txtItemDesc.getText().trim();
        String cantStr = txtCantidad.getText().trim();
        String valStr = txtValorUnitario.getText().trim();

        btnCambiarEstado.setEnabled(false);
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // 1. Actualizar estado de la cita y su historial
                citaControlador.actualizarEstado(cita.idCita(), nuevoEstado, observacion, usuarioLogueado.correo());

                // 2. Si el usuario ingresó un diagnóstico e ítems de cotización, registrarlos y enviarlos
                if (!diagnostico.isEmpty() && !descItem.isEmpty()) {
                    int cantidad = Integer.parseInt(cantStr);
                    java.math.BigDecimal valorUnitario = new java.math.BigDecimal(valStr);
                    String tipoDetalle = (String) cbxTipoDetalle.getSelectedItem();

                    long idMecanico = 1; // ID por defecto del mecánico o asignado
                    try (java.sql.Connection cn = com.tallermotosv1.conexion.ConexionDB.obtenerConexion();
                         java.sql.PreparedStatement ps = cn.prepareStatement("SELECT id_usuario FROM Usuario WHERE rol = 'MECANICO' LIMIT 1");
                         java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) idMecanico = rs.getLong(1);
                    } catch (Exception ignored) {}

                    com.tallermotosv1.dao.CotizacionDAO.Item item = new com.tallermotosv1.dao.CotizacionDAO.Item(tipoDetalle, descItem, cantidad, valorUnitario);
                    
                    new com.tallermotosv1.controladores.CotizacionControlador().generarYEnviarCotizacion(
                            cita.idCita(), idMecanico, cita.correo(), diagnostico, List.of(item)
                    );
                }
                return null;
            }

            @Override
            protected void done() {
                btnCambiarEstado.setEnabled(true);
                try {
                    get();
                    cargarCitas();
                    JOptionPane.showMessageDialog(DashboardFrame.this,
                            "Cita actualizada exitosamente a: " + nuevoEstado + (!diagnostico.isEmpty() ? " y cotización enviada al correo." : ""));
                    notificarCliente(cita, nuevoEstado);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();
                    String mensaje = (causa instanceof ReglaNegocioException)
                            ? causa.getMessage()
                            : (causa instanceof java.sql.SQLException)
                            ? "Error en base de datos: " + causa.getMessage()
                            : "Error inesperado: " + causa;
                    JOptionPane.showMessageDialog(DashboardFrame.this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void notificarCliente(CitaResumen cita, String nuevoEstado) {
        String correo = cita.correo();
        if (correo == null || correo.isBlank()) return;
        String asunto = "Actualización de Estado - Taller de Motos";
        String mensaje = "Hola " + cita.cliente() + ",\n\n"
                + "Te informamos que el estado de reparación de tu motocicleta de placa "
                + cita.placa() + " ha cambiado.\n\n"
                + "Estado anterior: " + cita.estado() + "\n"
                + "Nuevo estado: " + nuevoEstado + "\n\n"
                + "Puedes comunicarte con nosotros si tienes alguna duda.\n"
                + "¡Gracias por confiar en el Taller de Motos!";
        NotificadorEmail.enviarCorreoAsync(correo, asunto, mensaje,
                () -> registrarNotificacion("CAMBIO_ESTADO", true, cita.idCita()),
                motivo -> {
                    registrarNotificacion("CAMBIO_ESTADO", false, cita.idCita());
                    JOptionPane.showMessageDialog(this,
                            "Estado actualizado, pero hubo un error al enviar el correo: " + motivo,
                            "Error de Notificación", JOptionPane.WARNING_MESSAGE);
                });
    }

    private void registrarNotificacion(String tipo, boolean enviado, long idCita) {
        try {
            new NotificacionDAO().registrar(tipo, enviado, idCita);
        } catch (SQLException ex) {
            System.err.println("No se pudo guardar el resultado de la notificación: " + ex.getMessage());
        }
    }
}
