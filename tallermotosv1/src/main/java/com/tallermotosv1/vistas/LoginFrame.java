package com.tallermotosv1.vistas;

import com.tallermotosv1.datos.RepositorioDatos;
import com.tallermotosv1.modelo.Usuario;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtCorreo;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("Taller Motos - Iniciar Sesión");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setResizable(false); // Evita que el usuario deforme la ventana

        // Panel principal con un margen interno
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridBagLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0); // Espaciado entre filas
        gbc.gridx = 0;

        // Título de Bienvenida
        JLabel lblTitulo = new JLabel("Bienvenido al Sistema", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(33, 37, 41)); // Color oscuro
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0); // Margen extra debajo del título
        panelPrincipal.add(lblTitulo, gbc);

        // Etiqueta Correo
        JLabel lblCorreo = new JLabel("Correo Electrónico:");
        lblCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        panelPrincipal.add(lblCorreo, gbc);

        // Campo de texto Correo
        txtCorreo = new JTextField();
        txtCorreo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCorreo.putClientProperty("JTextField.placeholderText", "ejemplo@taller.com"); // Placeholder moderno
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        panelPrincipal.add(txtCorreo, gbc);

        // Etiqueta Contraseña
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        panelPrincipal.add(lblPass, gbc);

        // Campo de texto Contraseña
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 25, 0);
        panelPrincipal.add(txtPassword, gbc);

        // Botón de Ingresar
        JButton btnLogin = new JButton("INGRESAR");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(0, 123, 255)); // Azul moderno
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Cursor tipo mano
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 0, 0);
        panelPrincipal.add(btnLogin, gbc);

        add(panelPrincipal);

        // Acción del botón (Evento)
        btnLogin.addActionListener(e -> validarLogin());
        
        // Permitir presionar "Enter" para iniciar sesión
        getRootPane().setDefaultButton(btnLogin);
    }

    private void validarLogin() {
        String correo = txtCorreo.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (correo.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese su correo y contraseña.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar contra los datos quemados
        for (Usuario u : RepositorioDatos.usuarios) {
            if (u.getCorreo().equals(correo) && u.getContrasena().equals(pass)) {
                // Login exitoso: Abrimos el Dashboard
                new DashboardFrame(u).setVisible(true);
                this.dispose(); // Cerramos la ventana de Login
                return;
            }
        }
        
        // Si el ciclo termina, no encontró el usuario
        JOptionPane.showMessageDialog(this, "Correo o contraseña incorrectos.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        txtPassword.setText(""); // Limpia la contraseña en caso de error
    }
}