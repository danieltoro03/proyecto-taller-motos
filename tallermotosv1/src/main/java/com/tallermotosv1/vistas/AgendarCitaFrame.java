package com.tallermotosv1.vistas;

import com.tallermotosv1.datos.RepositorioDatos;
import com.tallermotosv1.enums.EstadoCita;
import com.tallermotosv1.modelo.Cita;
import com.tallermotosv1.modelo.Cliente;
import com.tallermotosv1.modelo.Moto;
import com.tallermotosv1.utilidades.NotificadorEmail;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class AgendarCitaFrame extends JFrame {
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtDocumento;
    private JTextField txtCorreo;
    private JTextField txtTelefono;
    private JTextField txtPlaca;
    private JTextField txtMarca;
    
    // Nuevos componentes para la fecha y hora
    private JDateChooser dateChooser;
    private JComboBox<String> cbxHora;

    public AgendarCitaFrame() {
        setTitle("Registrar Nueva Cita");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Título de la ventana
        JLabel lblTitulo = new JLabel("REGISTRO DE CITA Y CLIENTE", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(33, 37, 41));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel central con GridBagLayout para organizar los campos
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.gridwidth = 2;

        // --- SECCIÓN: DATOS DEL CLIENTE ---
        JLabel lblSeccionCliente = new JLabel("Datos del Cliente");
        lblSeccionCliente.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSeccionCliente.setForeground(new Color(0, 102, 204));
        panelForm.add(lblSeccionCliente, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panelForm.add(new JLabel("Nombres:"), gbc);
        gbc.gridx = 1;
        txtNombres = new JTextField(15);
        panelForm.add(txtNombres, gbc);

        gbc.gridx = 0; 
        gbc.gridy++;
        panelForm.add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1;
        txtApellidos = new JTextField(15);
        panelForm.add(txtApellidos, gbc);

        gbc.gridx = 0; 
        gbc.gridy++;
        panelForm.add(new JLabel("Documento:"), gbc);
        gbc.gridx = 1;
        txtDocumento = new JTextField(15);
        panelForm.add(txtDocumento, gbc);

        gbc.gridx = 0; 
        gbc.gridy++;
        panelForm.add(new JLabel("Correo Electrónico:"), gbc);
        gbc.gridx = 1;
        txtCorreo = new JTextField(15);
        panelForm.add(txtCorreo, gbc);

        gbc.gridx = 0; 
        gbc.gridy++;
        panelForm.add(new JLabel("Teléfono/Celular:"), gbc);
        gbc.gridx = 1;
        txtTelefono = new JTextField(15);
        panelForm.add(txtTelefono, gbc);

        // --- SECCIÓN: DATOS DE LA MOTO ---
        gbc.gridx = 0; 
        gbc.gridy++; 
        gbc.gridwidth = 2;
        JLabel lblSeccionMoto = new JLabel("Datos de la Motocicleta");
        lblSeccionMoto.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSeccionMoto.setForeground(new Color(0, 102, 204));
        panelForm.add(lblSeccionMoto, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; 
        gbc.gridy++;
        panelForm.add(new JLabel("Placa:"), gbc);
        gbc.gridx = 1;
        txtPlaca = new JTextField(15);
        panelForm.add(txtPlaca, gbc);

        gbc.gridx = 0; 
        gbc.gridy++;
        panelForm.add(new JLabel("Marca:"), gbc);
        gbc.gridx = 1;
        txtMarca = new JTextField(15);
        panelForm.add(txtMarca, gbc);

        // --- SECCIÓN: CITA ---
        gbc.gridx = 0; 
        gbc.gridy++; 
        gbc.gridwidth = 2;
        JLabel lblSeccionCita = new JLabel("Detalles de la Cita");
        lblSeccionCita.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSeccionCita.setForeground(new Color(0, 102, 204));
        panelForm.add(lblSeccionCita, gbc);

        // --- CALENDARIO VISUAL (JDateChooser) ---
        gbc.gridwidth = 1;
        gbc.gridx = 0; 
        gbc.gridy++;
        panelForm.add(new JLabel("Fecha de la Cita:"), gbc);
        gbc.gridx = 1;

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd"); // Formato visual
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Poner la fecha de mañana por defecto
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        dateChooser.setDate(cal.getTime());
        
        panelForm.add(dateChooser, gbc);

        // --- SELECTOR DE HORA (JComboBox) ---
        gbc.gridx = 0; 
        gbc.gridy++;
        panelForm.add(new JLabel("Hora de la Cita:"), gbc);
        gbc.gridx = 1;

        String[] horas = {
            "08:00 AM", "08:30 AM", "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", 
            "11:00 AM", "11:30 AM", "12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM", 
            "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM", 
            "05:00 PM"
        };
        cbxHora = new JComboBox<>(horas);
        cbxHora.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelForm.add(cbxHora, gbc);

        add(new JScrollPane(panelForm), BorderLayout.CENTER);

        // Botón Guardar
        JPanel panelBotones = new JPanel();
        panelBotones.setBackground(Color.WHITE);
        JButton btnGuardar = new JButton("Guardar y Enviar Correo");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setBackground(new Color(0, 153, 51)); // Verde
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelBotones.add(btnGuardar);
        add(panelBotones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardarCita());
    }

        private void guardarCita() {
        try {
            // 1. Validar capacidad máxima (Límite: 6)
            long citasActivas = RepositorioDatos.citas.stream()
                    .filter(c -> c.getEstado() == EstadoCita.PENDIENTE || c.getEstado() == EstadoCita.EN_PROCESO)
                    .count();

            if (citasActivas >= 6) {
                JOptionPane.showMessageDialog(this, 
                        "Capacidad máxima alcanzada en el taller (Límite: 6 vehículos).", 
                        "Alerta de Capacidad", 
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Validar campos
            if (dateChooser.getDate() == null || txtNombres.getText().isEmpty() || txtDocumento.getText().isEmpty() || txtPlaca.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor complete los campos y seleccione una fecha.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. --- NUEVA LÓGICA DE FECHA A PRUEBA DE ERRORES ---
            // Extraemos el día, mes y año del calendario visual
            Date fechaCalendario = dateChooser.getDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaCalendario);
            int anio = cal.get(Calendar.YEAR);
            int mes = cal.get(Calendar.MONTH) + 1; // En Java los meses van de 0 a 11
            int dia = cal.get(Calendar.DAY_OF_MONTH);

            // Extraemos la hora seleccionada del ComboBox
            String horaSeleccionada = (String) cbxHora.getSelectedItem();
            int hora = 0, minuto = 0;

            // Parseamos la hora manualmente para evitar conflictos con AM/PM
            if (horaSeleccionada != null) {
                // El formato siempre es "HH:mm a" (Ej: "01:30 PM")
                String[] partes = horaSeleccionada.split(" "); // ["01:30", "PM"]
                String[] horaMinuto = partes[0].split(":"); // ["01", "30"]
                
                hora = Integer.parseInt(horaMinuto[0]);
                minuto = Integer.parseInt(horaMinuto[1]);
                String ampm = partes[1];

                // Convertir a formato de 24 horas
                if (ampm.equals("PM") && hora != 12) {
                    hora += 12;
                } else if (ampm.equals("AM") && hora == 12) {
                    hora = 0; // 12 AM es medianoche (00:00)
                }
            }

            // Creamos el LocalDateTime exacto sin usar SimpleDateFormat
            LocalDateTime fecha = LocalDateTime.of(anio, mes, dia, hora, minuto);
            // -----------------------------------------------------

            // 4. Crear los objetos del modelo
            Cliente cliente = new Cliente(
                    RepositorioDatos.citas.size() + 1, 
                    txtDocumento.getText(), 
                    txtNombres.getText() + " " + txtApellidos.getText(), 
                    txtTelefono.getText(), 
                    txtCorreo.getText()
            );
            
            Moto moto = new Moto(
                    RepositorioDatos.citas.size() + 1, 
                    txtPlaca.getText(), 
                    txtMarca.getText(), 
                    "N/A", 0, "N/A", cliente
            );
            
            Cita nuevaCita = new Cita(
                    RepositorioDatos.citas.size() + 1, 
                    fecha, 
                    EstadoCita.PENDIENTE, 
                    "Cita Agendada", 
                    moto, 
                    null
            );
            
            // 5. Guardar en la "Base de Datos" simulada
            RepositorioDatos.citas.add(nuevaCita);

            // 6. Enviar correo electrónico (RF04)
            // Para el correo, formateamos el LocalDateTime a un String legible:
            String fechaTexto = String.format("%02d/%02d/%d a las %s", dia, mes, anio, horaSeleccionada);
            
            NotificadorEmail.enviarCorreo(cliente.getCorreo(), 
                    "Confirmación de Cita - Taller de Motos", 
                    "Hola " + cliente.getNombre() + ",\n\nSu cita para la moto de placa " + moto.getPlaca() + 
                    " ha sido agendada para el " + fechaTexto + ".\n\n¡Gracias por confiar en nosotros!");

            // 7. Mensaje de éxito y cerrar ventana
            JOptionPane.showMessageDialog(this, "Cita registrada exitosamente y correo enviado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            this.dispose(); 

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}