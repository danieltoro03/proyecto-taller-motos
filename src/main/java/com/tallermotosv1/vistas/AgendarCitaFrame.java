package com.tallermotosv1.vistas;

import com.tallermotosv1.controladores.CitaControlador;
import com.tallermotosv1.excepciones.ReglaNegocioException;
import com.tallermotosv1.dto.ClienteDatos;
import com.tallermotosv1.dto.MotoDatos;
import com.tallermotosv1.utilidades.NotificadorEmail;
import com.tallermotosv1.utilidades.Validador;
import com.tallermotosv1.dao.NotificacionDAO;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class AgendarCitaFrame extends JFrame {
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter FORMATO_FECHA_CORREO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' hh:mm a", Locale.ENGLISH);

    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtDocumento;
    private JTextField txtCorreo;
    private JTextField txtTelefono;
    private JTextField txtPlaca;
    private JTextField txtMarca;
    private JTextField txtMotivo;
    private JDateChooser dateChooser;
    private JComboBox<LocalTime> cbxHora;
    private JButton btnGuardar;

    private final CitaControlador citaControlador = new CitaControlador();
    private final Runnable onGuardado; // se ejecuta al guardar (p. ej. refrescar el Dashboard)
    private final String correoRecepcionista;

    public AgendarCitaFrame() {
        this(null, null);
    }

    public AgendarCitaFrame(Runnable onGuardado) {
        this(onGuardado, null);
    }

    public AgendarCitaFrame(Runnable onGuardado, String correoRecepcionista) {
        this.onGuardado = onGuardado;
        this.correoRecepcionista = correoRecepcionista;
        setTitle("Taller de Motos");
        setSize(900, 760);
        setMinimumSize(new Dimension(760, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 12));
        getContentPane().setBackground(EstiloUI.FONDO);

        // Título de la ventana
        JLabel lblTitulo = new JLabel("Agendar cita");
        lblTitulo.setFont(EstiloUI.TITULO);
        lblTitulo.setForeground(EstiloUI.TEXTO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(22, 28, 5, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel central con GridBagLayout para organizar los campos
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        // --- SECCIÓN: DATOS DEL CLIENTE ---
        panelForm.add(crearTituloSeccion("Datos del Cliente"), gbc);

        txtNombres = agregarFila(panelForm, gbc, "Nombres:");
        txtApellidos = agregarFila(panelForm, gbc, "Apellidos:");
        txtDocumento = agregarFila(panelForm, gbc, "Documento:");
        txtCorreo = agregarFila(panelForm, gbc, "Correo Electrónico:");
        txtTelefono = agregarFila(panelForm, gbc, "Teléfono/Celular:");

        // --- SECCIÓN: DATOS DE LA MOTO ---
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panelForm.add(crearTituloSeccion("Datos de la Motocicleta"), gbc);

        txtPlaca = agregarFila(panelForm, gbc, "Placa:");
        txtMarca = agregarFila(panelForm, gbc, "Marca:");

        // --- SECCIÓN: CITA ---
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panelForm.add(crearTituloSeccion("Detalles de la Cita"), gbc);

        // Calendario visual
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;
        panelForm.add(new JLabel("Fecha de la Cita:"), gbc);
        gbc.gridx = 1;

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateChooser.setMinSelectableDate(aDate(LocalDate.now())); // no permite días pasados
        dateChooser.setDate(aDate(LocalDate.now().plusDays(1)));  // mañana por defecto
        panelForm.add(dateChooser, gbc);

        // Selector de hora (08:00 AM a 05:00 PM, cada 30 min)
        gbc.gridx = 0;
        gbc.gridy++;
        panelForm.add(new JLabel("Hora de la Cita:"), gbc);
        gbc.gridx = 1;

        cbxHora = new JComboBox<>();
        for (LocalTime h = LocalTime.of(8, 0); !h.isAfter(LocalTime.of(17, 0)); h = h.plusMinutes(30)) {
            cbxHora.addItem(h);
        }
        cbxHora.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbxHora.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Object texto = (value instanceof LocalTime t) ? FORMATO_HORA.format(t) : value;
                return super.getListCellRendererComponent(list, texto, index, isSelected, cellHasFocus);
            }
        });
        panelForm.add(cbxHora, gbc);

        txtMotivo = agregarFila(panelForm, gbc, "Motivo de ingreso:");

        JScrollPane scrollForm = new JScrollPane(panelForm);
        scrollForm.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 22, 0, 22), BorderFactory.createLineBorder(EstiloUI.BORDE)));
        scrollForm.getViewport().setBackground(Color.WHITE);
        add(scrollForm, BorderLayout.CENTER);

        // Botón Guardar
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(EstiloUI.FONDO);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 20, 18, 20));
        btnGuardar = new JButton("Guardar y Enviar Correo");
        btnGuardar.setFont(EstiloUI.MEDIANA);
        btnGuardar.setBackground(EstiloUI.NARANJA);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelBotones.add(btnGuardar);
        add(panelBotones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardarCita());
    }

    private JLabel crearTituloSeccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(190, 83, 25));
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        return lbl;
    }

    /** Agrega una fila "etiqueta + campo de texto" y devuelve el campo. */
    private JTextField agregarFila(JPanel panel, GridBagConstraints gbc, String etiqueta) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        JTextField campo = new JTextField(15);
        campo.setFont(EstiloUI.NORMAL);
        campo.setPreferredSize(new Dimension(260, 34));
        panel.add(campo, gbc);
        return campo;
    }

    private static Date aDate(LocalDate fecha) {
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private void advertencia(String mensaje, JComponent campoAEnfocar) {
        JOptionPane.showMessageDialog(this, mensaje, "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        if (campoAEnfocar != null) {
            campoAEnfocar.requestFocusInWindow();
        }
    }

    private void guardarCita() {
        // 1. Leer y validar los campos
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String documento = txtDocumento.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String placa = txtPlaca.getText().trim().toUpperCase();
        String marca = txtMarca.getText().trim();
        String motivo = txtMotivo.getText().trim();
        if (motivo.isEmpty()) {
            motivo = "Cita Agendada";
        }

        if (nombres.isEmpty()) {
            advertencia("Ingrese los nombres del cliente.", txtNombres);
            return;
        }
        if (apellidos.isEmpty()) {
            advertencia("Ingrese los apellidos del cliente.", txtApellidos);
            return;
        }
        if (!Validador.esDocumentoValido(documento)) {
            advertencia("El documento debe tener solo números (entre 5 y 15 dígitos).", txtDocumento);
            return;
        }
        if (!Validador.esCorreoValido(correo)) {
            advertencia("Ingrese un correo electrónico válido (ej: nombre@correo.com).", txtCorreo);
            return;
        }
        if (!Validador.esTelefonoValido(telefono)) {
            advertencia("El teléfono debe tener solo números (entre 7 y 15 dígitos).", txtTelefono);
            return;
        }
        if (!Validador.esPlacaValida(placa)) {
            advertencia("La placa no es válida. Formato esperado: ABC12D (3 letras, 2 números, 1 letra o número).", txtPlaca);
            return;
        }
        if (marca.isEmpty()) {
            advertencia("Ingrese la marca de la motocicleta.", txtMarca);
            return;
        }
        if (dateChooser.getDate() == null) {
            advertencia("Seleccione la fecha de la cita.", dateChooser);
            return;
        }
        LocalTime hora = (LocalTime) cbxHora.getSelectedItem();
        if (hora == null) {
            advertencia("Seleccione la hora de la cita.", cbxHora);
            return;
        }

        // 2. Fecha y hora exactas (sin parsear textos AM/PM)
        LocalDate dia = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        final LocalDateTime fecha = LocalDateTime.of(dia, hora);
        if (fecha.isBefore(LocalDateTime.now())) {
            advertencia("La fecha y hora de la cita ya pasaron. Elija una posterior.", dateChooser);
            return;
        }

        // 3. Objetos del modelo (los ids los genera la base de datos)
        final ClienteDatos cliente = new ClienteDatos(documento, nombres + " " + apellidos, telefono, correo);
        final MotoDatos moto = new MotoDatos(placa, marca, "N/A", 0, "N/A");
        final String motivoFinal = motivo;

        // 4. Guardar en la BD fuera del hilo de la interfaz
        btnGuardar.setEnabled(false);
        new SwingWorker<Long, Void>() {
            @Override
            protected Long doInBackground() throws Exception {
                return citaControlador.agendarCita(cliente, moto, fecha, motivoFinal, correoRecepcionista);
            }

            @Override
            protected void done() {
                btnGuardar.setEnabled(true);
                try {
                    long idCita = get();
                    alGuardar(cliente, moto, fecha, idCita);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();
                    if (causa instanceof ReglaNegocioException) {
                        JOptionPane.showMessageDialog(AgendarCitaFrame.this, causa.getMessage(),
                                "No se pudo agendar", JOptionPane.WARNING_MESSAGE);
                    } else if (causa instanceof SQLException) {
                        JOptionPane.showMessageDialog(AgendarCitaFrame.this,
                                "Error de base de datos: " + causa.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(AgendarCitaFrame.this,
                                "Error inesperado al guardar: " + causa,
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.execute();
    }

    private void alGuardar(ClienteDatos cliente, MotoDatos moto, LocalDateTime fecha, long idCita) {
        // Correo en segundo plano (RF04): la ventana no se congela
        String mensaje = "Hola " + cliente.nombre() + ",\n\nSu cita para la moto de placa " + moto.placa()
                + " ha sido agendada para el " + fecha.format(FORMATO_FECHA_CORREO)
                + ".\n\n¡Gracias por confiar en nosotros!";
        NotificadorEmail.enviarCorreoAsync(cliente.correo(), "Confirmación de Cita - Taller de Motos", mensaje,
                () -> registrarNotificacion("CONFIRMACION_CITA", true, idCita),
                motivoError -> {
                    registrarNotificacion("CONFIRMACION_CITA", false, idCita);
                    JOptionPane.showMessageDialog(this,
                            "La cita se guardó, pero no se pudo enviar el correo a " + cliente.correo()
                                    + ":\n" + motivoError,
                            "Error de Notificación", JOptionPane.WARNING_MESSAGE);
                });

        JOptionPane.showMessageDialog(this,
                "Cita registrada exitosamente. Se está enviando la confirmación a " + cliente.correo() + ".",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        if (onGuardado != null) {
            onGuardado.run();
        }
        dispose();
    }

    private void registrarNotificacion(String tipo, boolean enviado, long idCita) {
        try {
            new NotificacionDAO().registrar(tipo, enviado, idCita);
        } catch (SQLException ex) {
            System.err.println("No se pudo guardar el resultado de la notificación: " + ex.getMessage());
        }
    }
}
