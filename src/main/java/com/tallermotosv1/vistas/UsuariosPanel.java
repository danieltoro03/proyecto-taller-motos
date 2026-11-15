package com.tallermotosv1.vistas;

import com.tallermotosv1.controladores.UsuarioControlador;
import com.tallermotosv1.dto.UsuarioResumen;
import com.tallermotosv1.dto.UsuarioSesion;
import com.tallermotosv1.utilidades.TareaSegundoPlano;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UsuariosPanel extends JPanel {
    private final UsuarioControlador controlador = new UsuarioControlador();
    private final UsuarioSesion usuarioActual;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private List<UsuarioResumen> usuarios = new ArrayList<>();

    public UsuariosPanel(UsuarioSesion usuarioActual) {
        this.usuarioActual = usuarioActual;
        setLayout(new BorderLayout(0, 10));
        setBackground(EstiloUI.FONDO);
        setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));

        JLabel titulo = new JLabel("Usuarios del Sistema");
        titulo.setFont(EstiloUI.TITULO);
        titulo.setForeground(EstiloUI.TEXTO);
        add(titulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Correo", "Rol"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        EstiloUI.estilizarTabla(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.setBackground(EstiloUI.FONDO);
        
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnRol = new JButton("Cambiar Rol");
        JButton btnClave = new JButton("Restablecer Contraseña");
        JButton btnNuevo = new JButton("Nuevo Usuario");
        btnNuevo.setBackground(EstiloUI.NARANJA);
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);

        acciones.add(btnActualizar);
        acciones.add(btnRol);
        acciones.add(btnClave);
        acciones.add(btnNuevo);
        add(acciones, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> cargar());
        btnNuevo.addActionListener(e -> nuevoUsuario());
        btnClave.addActionListener(e -> restablecerContrasena());
        btnRol.addActionListener(e -> cambiarRol());

        cargar();
    }

    private void cargar() {
        TareaSegundoPlano.ejecutar(this, controlador::listarUsuarios, lista -> {
            usuarios = lista;
            modelo.setRowCount(0);
            for (UsuarioResumen u : lista) {
                modelo.addRow(new Object[]{u.idUsuario(), u.nombre(), u.correo(), u.rol()});
            }
        });
    }

    private UsuarioResumen seleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.", "Selección Requerida", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return usuarios.get(tabla.convertRowIndexToModel(fila));
    }

    private void nuevoUsuario() {
        JTextField txtNombre = new JTextField(20);
        JTextField txtCorreo = new JTextField(20);
        JPasswordField txtClave = new JPasswordField(20);
        JPasswordField txtClave2 = new JPasswordField(20);
        JComboBox<String> cbxRol = new JComboBox<>(new String[]{"ADMINISTRADOR", "MECANICO", "RECEPCIONISTA"});
        cbxRol.setSelectedItem("RECEPCIONISTA");

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 4));
        panel.add(new JLabel("Nombre completo:")); panel.add(txtNombre);
        panel.add(new JLabel("Correo electrónico:")); panel.add(txtCorreo);
        panel.add(new JLabel("Contraseña:")); panel.add(txtClave);
        panel.add(new JLabel("Repetir contraseña:")); panel.add(txtClave2);
        panel.add(new JLabel("Rol:")); panel.add(cbxRol);

        int op = JOptionPane.showConfirmDialog(this, panel, "Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            String n = txtNombre.getText().trim();
            String c = txtCorreo.getText().trim();
            String k = new String(txtClave.getPassword());
            String rol = (String) cbxRol.getSelectedItem();
            TareaSegundoPlano.ejecutar(this, () -> {
                controlador.registrarUsuario(n, c, k, rol);
                return null;
            }, r -> {
                cargar();
                JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.");
            });
        }
    }

    private void restablecerContrasena() {
        UsuarioResumen u = seleccionado();
        if (u == null) return;
        JPasswordField txtClave = new JPasswordField(20);
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 4));
        panel.add(new JLabel("Nueva contraseña para " + u.correo() + ":"));
        panel.add(txtClave);

        int op = JOptionPane.showConfirmDialog(this, panel, "Restablecer Contraseña", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            String k = new String(txtClave.getPassword());
            TareaSegundoPlano.ejecutar(this, () -> {
                controlador.restablecerContrasena(u, k);
                return null;
            }, r -> JOptionPane.showMessageDialog(this, "Contraseña actualizada."));
        }
    }

    private void cambiarRol() {
        UsuarioResumen u = seleccionado();
        if (u == null) return;
        JComboBox<String> cbxRol = new JComboBox<>(new String[]{"ADMINISTRADOR", "MECANICO", "RECEPCIONISTA"});
        cbxRol.setSelectedItem(u.rol());
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 4));
        panel.add(new JLabel("Nuevo rol para " + u.correo() + ":"));
        panel.add(cbxRol);

        int op = JOptionPane.showConfirmDialog(this, panel, "Cambiar Rol", JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            String nuevo = (String) cbxRol.getSelectedItem();
            TareaSegundoPlano.ejecutar(this, () -> {
                controlador.cambiarRol(u, nuevo, usuarioActual.correo());
                return null;
            }, r -> {
                cargar();
                JOptionPane.showMessageDialog(this, "Rol actualizado a " + nuevo);
            });
        }
    }
}
