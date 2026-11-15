package com.tallermotosv1.vistas;

import com.tallermotosv1.controladores.UsuarioControlador;
import com.tallermotosv1.dto.UsuarioSesion;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class LoginFrame extends JFrame {
    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private final UsuarioControlador usuarioControlador = new UsuarioControlador();

    public LoginFrame() {
        setTitle("Taller de Motos");
        setSize(1050, 680);
        setMinimumSize(new Dimension(850, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridLayout(1, 2));
        root.add(crearPanelMarca());
        root.add(crearPanelFormulario());
        setContentPane(root);
        getRootPane().setDefaultButton(btnLogin);
    }

    private JPanel crearPanelMarca() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(EstiloUI.OSCURO);
        panel.setBorder(new EmptyBorder(34, 42, 38, 42));

        JLabel marca = new JLabel("Taller de Motos");
        marca.setForeground(Color.WHITE);
        marca.setFont(new Font("Segoe UI", Font.BOLD, 22));
        panel.add(marca, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.add(Box.createVerticalGlue());
        JLabel titulo = new JLabel("Cada moto, bajo");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        JLabel titulo2 = new JLabel("control desde que llega.");
        titulo2.setForeground(Color.WHITE);
        titulo2.setFont(new Font("Segoe UI", Font.BOLD, 36));
        JLabel descripcion = new JLabel("Agenda citas, sigue reparaciones y mantén" );
        descripcion.setForeground(new Color(190, 194, 201));
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JLabel descripcion2 = new JLabel("a tus clientes informados en cada etapa.");
        descripcion2.setForeground(new Color(190, 194, 201));
        descripcion2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        centro.add(titulo); centro.add(titulo2);
        centro.add(Box.createVerticalStrut(17));
        centro.add(descripcion); centro.add(descripcion2);
        centro.add(Box.createVerticalStrut(28));
        centro.add(punto("01", "Agenda con control de capacidad"));
        centro.add(Box.createVerticalStrut(12));
        centro.add(punto("02", "Seguimiento de estados de reparación"));
        centro.add(Box.createVerticalStrut(12));
        centro.add(punto("03", "Notificaciones por correo electrónico"));
        centro.add(Box.createVerticalGlue());
        panel.add(centro, BorderLayout.CENTER);
        JLabel pie = new JLabel("GESTIÓN INTELIGENTE PARA TU TALLER");
        pie.setForeground(new Color(130, 135, 145));
        pie.setFont(new Font("Segoe UI", Font.BOLD, 10));
        panel.add(pie, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel punto(String numero, String texto) {
        JLabel l = new JLabel("<html><span style='color:#FF6818'><b>" + numero + "</b></span>&nbsp;&nbsp; " + texto + "</html>");
        l.setForeground(new Color(225, 227, 231));
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return l;
    }

    private JPanel crearPanelFormulario() {
        JPanel exterior = new JPanel(new GridBagLayout());
        exterior.setBackground(Color.WHITE);
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(350, 350));

        JLabel titulo = new JLabel("Iniciar sesión");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 29));
        titulo.setForeground(EstiloUI.TEXTO);
        JLabel subtitulo = new JLabel("Ingresa con tu cuenta del taller para continuar.");
        subtitulo.setFont(EstiloUI.NORMAL);
        subtitulo.setForeground(EstiloUI.SECUNDARIO);
        form.add(titulo); form.add(Box.createVerticalStrut(8)); form.add(subtitulo);
        form.add(Box.createVerticalStrut(30));
        form.add(etiqueta("Correo electrónico"));
        txtCorreo = campo("nombre@taller.com");
        form.add(txtCorreo); form.add(Box.createVerticalStrut(19));
        form.add(etiqueta("Contraseña"));
        txtPassword = new JPasswordField();
        configurarCampo(txtPassword);
        form.add(txtPassword); form.add(Box.createVerticalStrut(22));
        btnLogin = EstiloUI.boton("Ingresar al sistema", true);
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnLogin.addActionListener(e -> validarLogin());
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(18));
        JLabel ayuda = new JLabel("¿Problemas para ingresar? Contacta al administrador.");
        ayuda.setForeground(EstiloUI.SECUNDARIO);
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        form.add(ayuda);
        exterior.add(form);
        return exterior;
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(EstiloUI.TEXTO);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField campo(String placeholder) {
        JTextField f = new JTextField();
        f.putClientProperty("JTextField.placeholderText", placeholder);
        configurarCampo(f);
        return f;
    }

    private void configurarCampo(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        f.setPreferredSize(new Dimension(350, 45));
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(EstiloUI.BORDE), new EmptyBorder(8, 12, 8, 12)));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void validarLogin() {
        final String correo = txtCorreo.getText().trim();
        final char[] passChars = txtPassword.getPassword();
        final String pass = new String(passChars);
        Arrays.fill(passChars, '\0');
        if (correo.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese su correo y contraseña.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        btnLogin.setEnabled(false);
        new SwingWorker<Optional<UsuarioSesion>, Void>() {
            protected Optional<UsuarioSesion> doInBackground() throws Exception { return usuarioControlador.autenticar(correo, pass); }
            protected void done() {
                btnLogin.setEnabled(true);
                try {
                    Optional<UsuarioSesion> usuario = get();
                    if (usuario.isPresent()) { new DashboardFrame(usuario.get()).setVisible(true); dispose(); }
                    else { JOptionPane.showMessageDialog(LoginFrame.this, "Correo o contraseña incorrectos.", "Error de autenticación", JOptionPane.ERROR_MESSAGE); txtPassword.setText(""); }
                } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                catch (ExecutionException ex) { JOptionPane.showMessageDialog(LoginFrame.this, "No se pudo validar el acceso: " + ex.getCause().getMessage(), "Error de conexión", JOptionPane.ERROR_MESSAGE); }
            }
        }.execute();
    }
}
