package com.tallermotosv1;

import com.formdev.flatlaf.FlatLightLaf;
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

        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}