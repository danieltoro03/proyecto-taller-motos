package com.tallermotosv1.vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

/** Estilos compartidos de Taller de Motos. No contiene lógica de negocio. */
final class EstiloUI {
    static final Color OSCURO = new Color(20, 23, 28);
    static final Color NARANJA = new Color(255, 104, 24);
    static final Color FONDO = new Color(244, 245, 248);
    static final Color TEXTO = new Color(32, 34, 37);
    static final Color SECUNDARIO = new Color(112, 118, 127);
    static final Color BORDE = new Color(226, 228, 232);
    static final Font NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font MEDIANA = new Font("Segoe UI", Font.BOLD, 14);
    static final Font TITULO = new Font("Segoe UI", Font.BOLD, 25);

    private EstiloUI() {}

    static JButton boton(String texto, boolean principal) {
        JButton b = new JButton(texto);
        b.setFont(MEDIANA);
        b.setForeground(principal ? Color.WHITE : TEXTO);
        b.setBackground(principal ? NARANJA : Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(principal ? NARANJA : BORDE),
                new EmptyBorder(10, 17, 10, 17)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static JPanel tarjeta() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE), new EmptyBorder(16, 18, 16, 18)));
        return p;
    }

    static void estilizarTabla(JTable tabla) {
        tabla.setFont(NORMAL);
        tabla.setForeground(TEXTO);
        tabla.setBackground(Color.WHITE);
        tabla.setSelectionBackground(new Color(255, 239, 228));
        tabla.setSelectionForeground(TEXTO);
        tabla.setRowHeight(42);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(239, 240, 243));
        tabla.setIntercellSpacing(new Dimension(0, 1));
        JTableHeader h = tabla.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 11));
        h.setForeground(SECUNDARIO);
        h.setBackground(new Color(249, 250, 252));
        h.setPreferredSize(new Dimension(0, 42));
        h.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE));
    }
}
