package com.tallermotosv1.vistas;

import com.tallermotosv1.controladores.CitaControlador;
import com.tallermotosv1.dto.ClienteDatos;
import com.tallermotosv1.dto.MotoDatos;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class AgendarCitaPanel extends JPanel {

    private JTextField txtNombres, txtApellidos, txtDocumento, txtCorreo, txtTelefono;
    private JTextField txtPlaca, txtMarca, txtModelo, txtCilindraje;
    private JComboBox<String> cbxTipoMoto;
    private JTextField txtMotivo;
    private JDateChooser dateChooser;
    private JComboBox<LocalTime> cbxHora;
    private JButton btnGuardar;

    private final CitaControlador citaControlador = new CitaControlador();
    private final String correoUsuario;
    private final Runnable onExito;

    public AgendarCitaPanel(String correoUsuario, Runnable onExito) {
        this.correoUsuario = correoUsuario;
        this.onExito = onExito;
        setLayout(new BorderLayout(0, 10));
        setBackground(EstiloUI.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        initComponents();
    }

    private void initComponents() {
        JLabel titulo = new JLabel("Agendar Nueva Cita");
        titulo.setFont(EstiloUI.TITULO);
        add(titulo, BorderLayout.NORTH);

        JPanel panelForm = new JPanel(new GridLayout(0, 2, 10, 10));
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        txtDocumento = new JTextField(); txtNombres = new JTextField();
        txtApellidos = new JTextField(); txtCorreo = new JTextField(); txtTelefono = new JTextField();
        txtPlaca = new JTextField(); txtMarca = new JTextField(); txtModelo = new JTextField();
        txtCilindraje = new JTextField("125");
        cbxTipoMoto = new JComboBox<>(new String[]{"SCOOTER", "URBANA", "ENDURO", "TOURING", "SPORT", "DEPORTIVA"});
        txtMotivo = new JTextField();

        dateChooser = new JDateChooser(new Date());
        cbxHora = new JComboBox<>();
        for (LocalTime h = LocalTime.of(8, 0); !h.isAfter(LocalTime.of(17, 0)); h = h.plusMinutes(30)) {
            cbxHora.addItem(h);
        }

        panelForm.add(new JLabel("Documento:")); panelForm.add(txtDocumento);
        panelForm.add(new JLabel("Nombres:")); panelForm.add(txtNombres);
        panelForm.add(new JLabel("Apellidos:")); panelForm.add(txtApellidos);
        panelForm.add(new JLabel("Correo:")); panelForm.add(txtCorreo);
        panelForm.add(new JLabel("Teléfono:")); panelForm.add(txtTelefono);
        panelForm.add(new JLabel("Placa Moto:")); panelForm.add(txtPlaca);
        panelForm.add(new JLabel("Marca:")); panelForm.add(txtMarca);
        panelForm.add(new JLabel("Modelo:")); panelForm.add(txtModelo);
        panelForm.add(new JLabel("Cilindraje (cc):")); panelForm.add(txtCilindraje);
        panelForm.add(new JLabel("Tipo de Moto:")); panelForm.add(cbxTipoMoto);
        panelForm.add(new JLabel("Fecha:")); panelForm.add(dateChooser);
        panelForm.add(new JLabel("Hora:")); panelForm.add(cbxHora);
        panelForm.add(new JLabel("Motivo:")); panelForm.add(txtMotivo);

        add(new JScrollPane(panelForm), BorderLayout.CENTER);

        btnGuardar = new JButton("Guardar Cita");
        btnGuardar.setBackground(EstiloUI.NARANJA);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(EstiloUI.MEDIANA);
        btnGuardar.addActionListener(e -> guardarCita());

        JPanel pBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBoton.setBackground(EstiloUI.FONDO);
        pBoton.add(btnGuardar);
        add(pBoton, BorderLayout.SOUTH);
    }

    private void guardarCita() {
        try {
            ClienteDatos cliente = new ClienteDatos(
                    txtDocumento.getText().trim(),
                    txtNombres.getText().trim() + " " + txtApellidos.getText().trim(),
                    txtTelefono.getText().trim(),
                    txtCorreo.getText().trim()
            );

            int cc = Integer.parseInt(txtCilindraje.getText().trim());
            MotoDatos moto = new MotoDatos(
                    txtPlaca.getText().trim().toUpperCase(),
                    txtMarca.getText().trim(),
                    txtModelo.getText().trim(),
                    cc,
                    (String) cbxTipoMoto.getSelectedItem()
            );

            LocalDate fecha = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime hora = (LocalTime) cbxHora.getSelectedItem();
            LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);

            citaControlador.agendarCita(cliente, moto, fechaHora, txtMotivo.getText().trim(), correoUsuario);
            JOptionPane.showMessageDialog(this, "Cita agendada exitosamente.");
            if (onExito != null) onExito.run();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
