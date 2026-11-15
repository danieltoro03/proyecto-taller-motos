package com.tallermotosv1;

import com.formdev.flatlaf.FlatLightLaf;
import com.tallermotosv1.controladores.UsuarioControlador;
import com.tallermotosv1.vistas.LoginFrame;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        // Configurar el tema visual moderno (FlatLaf)
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("No se pudo cargar el tema visual");
        }

        crearUsuariosIniciales();

        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }

    /**
     * Si la tabla usuario está vacía crea los usuarios de prueba (con contraseña cifrada).
     * CAMBIE estas contraseñas antes de usar el sistema en serio.
     */
    private static void crearUsuariosIniciales() {
        try {
            UsuarioControlador usuarios = new UsuarioControlador();
            if (!usuarios.hayUsuarios()) {
                usuarios.crearUsuario("Administrador", "admin@taller.com", "1234", "ADMINISTRADOR");
                usuarios.crearUsuario("Mecánico", "mecanico@taller.com", "1234", "MECANICO");
                System.out.println("Usuarios iniciales creados (admin@taller.com y mecanico@taller.com, clave 1234).");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar con la base de datos:\n" + e.getMessage()
                            + "\n\nRevise config.properties y que MySQL esté encendido.",
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }
}
