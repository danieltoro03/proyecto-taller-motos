package com.tallermotosv1.vistas;

import com.tallermotosv1.controladores.UsuarioControlador;
import com.tallermotosv1.dto.UsuarioResumen;

import com.tallermotosv1.dto.UsuarioSesion;
import com.tallermotosv1.utilidades.TareaSegundoPlano;
import com.tallermotosv1.utilidades.Validador;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Gestión de usuarios del sistema (solo para administradores). */
public class UsuariosFrame extends JFrame {
    private final UsuarioControlador controlador = new UsuarioControlador();
    private final UsuarioSesion usuarioActual;
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private List<UsuarioResumen> usuarios = new ArrayList<>();

    public UsuariosFrame(UsuarioSesion usuarioActual) {
        this.usuarioActual = usuarioActual;
        setTitle("Taller de Motos");
        setSize(900, 600);
        getContentPane().setBackground(EstiloUI.FONDO);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(0, 10));
        main.setBackground(EstiloUI.FONDO);
        main.setBorder(BorderFactory.createEmptyBorder(25, 28, 25, 28));

        JLabel titulo = new JLabel("Usuarios del Sistema");
        titulo.setFont(EstiloUI.TITULO);
        titulo.setForeground(EstiloUI.TEXTO);
        main.add(titulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Correo", "Rol"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        EstiloUI.estilizarTabla(tabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        main.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNuevo = new JButton("Nuevo Usuario");
        btnNuevo.setBackground(new Color(0, 153, 51));
        btnNuevo.setForeground(Color.WHITE);
        JButton btnClave = new JButton("Restablecer Contraseña");
        JButton btnRol = new JButton("Cambiar Rol");
        JButton btnActualizar = new JButton("Actualizar");
        btnNuevo.setBackground(EstiloUI.NARANJA); btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        acciones.setBackground(EstiloUI.FONDO);
        acciones.add(btnActualizar);
        acciones.add(btnRol);
        acciones.add(btnClave);
        acciones.add(btnNuevo);
        main.add(acciones, BorderLayout.SOUTH);

        add(main);

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
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.", "Selección Requerida",
                    JOptionPane.WARNING_MESSAGE);
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
        panel.add(new JLabel("Nombre completo:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Correo electrónico:"));
        panel.add(txtCorreo);
        panel.add(new JLabel("Contraseña (mínimo " + UsuarioControlador.LARGO_MINIMO_CLAVE + " caracteres):"));
        panel.add(txtClave);
        panel.add(new JLabel("Repetir contraseña:"));
        panel.add(txtClave2);
        panel.add(new JLabel("Rol:"));
        panel.add(cbxRol);

        String nombre;
        String correo;
        String clave;
        while (true) { // se vuelve a mostrar con lo escrito si hay un error
            int op = JOptionPane.showConfirmDialog(this, panel, "Nuevo Usuario",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (op != JOptionPane.OK_OPTION) {
                return;
            }
            nombre = txtNombre.getText().trim();
            correo = txtCorreo.getText().trim();
            clave = new String(txtClave.getPassword());
            String repetida = new String(txtClave2.getPassword());

            String error = null;
            if (nombre.isEmpty()) {
                error = "Ingrese el nombre del usuario.";
            } else if (!Validador.esCorreoValido(correo)) {
                error = "Ingrese un correo electrónico válido.";
            } else if (!clave.equals(repetida)) {
                error = "Las contraseñas no coinciden.";
            } else {
                error = UsuarioControlador.validarContrasena(clave);
            }
            if (error == null) {
                break;
            }
            JOptionPane.showMessageDialog(this, error, "Datos inválidos", JOptionPane.WARNING_MESSAGE);
        }

        final String n = nombre;
        final String c = correo;
        final String k = clave;
        final String rol = (String) cbxRol.getSelectedItem();
        TareaSegundoPlano.ejecutar(this, () -> {
            controlador.registrarUsuario(n, c, k, rol);
            return null;
        }, r -> {
            cargar();
            JOptionPane.showMessageDialog(this, "Usuario creado: " + c);
        });
    }

    private void restablecerContrasena() {
        UsuarioResumen u = seleccionado();
        if (u == null) {
            return;
        }
        JPasswordField txtClave = new JPasswordField(20);
        JPasswordField txtClave2 = new JPasswordField(20);
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 4));
        panel.add(new JLabel("Nueva contraseña para " + u.correo() + ":"));
        panel.add(txtClave);
        panel.add(new JLabel("Repetir contraseña:"));
        panel.add(txtClave2);

        while (true) {
            int op = JOptionPane.showConfirmDialog(this, panel, "Restablecer Contraseña",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (op != JOptionPane.OK_OPTION) {
                return;
            }
            String clave = new String(txtClave.getPassword());
            String error = !clave.equals(new String(txtClave2.getPassword()))
                    ? "Las contraseñas no coinciden."
                    : UsuarioControlador.validarContrasena(clave);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Datos inválidos", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            TareaSegundoPlano.ejecutar(this, () -> {
                controlador.restablecerContrasena(u, clave);
                return null;
            }, r -> JOptionPane.showMessageDialog(this, "Contraseña actualizada para " + u.correo()));
            return;
        }
    }

    private void cambiarRol() {
        UsuarioResumen u = seleccionado();
        if (u == null) {
            return;
        }
        JComboBox<String> cbxRol = new JComboBox<>(new String[]{"ADMINISTRADOR", "MECANICO", "RECEPCIONISTA"});
        cbxRol.setSelectedItem(u.rol());
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 4));
        panel.add(new JLabel("Nuevo rol para " + u.correo() + ":"));
        panel.add(cbxRol);

        int op = JOptionPane.showConfirmDialog(this, panel, "Cambiar Rol",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        String nuevo = (String) cbxRol.getSelectedItem();
        if (op != JOptionPane.OK_OPTION || nuevo == null || nuevo.equals(u.rol())) {
            return;
        }
        TareaSegundoPlano.ejecutar(this, () -> {
            controlador.cambiarRol(u, nuevo, usuarioActual.correo());
            return null;
        }, r -> {
            cargar();
            JOptionPane.showMessageDialog(this, "Rol actualizado a " + nuevo + ".");
        });
    }
}
