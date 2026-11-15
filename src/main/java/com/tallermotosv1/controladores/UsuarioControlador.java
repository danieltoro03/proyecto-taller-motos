package com.tallermotosv1.controladores;

import com.tallermotosv1.dao.UsuarioDAO;
import com.tallermotosv1.dto.UsuarioResumen;
import com.tallermotosv1.dto.UsuarioSesion;
import com.tallermotosv1.excepciones.ReglaNegocioException;
import com.tallermotosv1.utilidades.Validador;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UsuarioControlador {
    public static final int LARGO_MINIMO_CLAVE = 8;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /** Devuelve el usuario (SIN hash) si el correo y la contraseña son correctos. */
    public Optional<UsuarioSesion> autenticar(String correo, String contrasena) throws SQLException {
        Optional<UsuarioSesion> encontrado = usuarioDAO.buscarPorCorreo(correo);
        if (encontrado.isEmpty()) {
            return Optional.empty();
        }
        UsuarioSesion u = encontrado.get();
        boolean ok;
        try {
            ok = BCrypt.checkpw(contrasena, u.contrasena());
        } catch (IllegalArgumentException e) { // hash inválido en la BD (p. ej. clave en texto plano)
            ok = false;
        }
        return ok ? Optional.of(new UsuarioSesion(u.correo(), null, u.rol())) : Optional.empty();
    }

    /** Crea un usuario sin validar la clave (uso interno: usuarios iniciales). */
    public void crearUsuario(String nombre, String correo, String contrasena, String rol) throws SQLException {
        usuarioDAO.crear(nombre, correo, BCrypt.hashpw(contrasena, BCrypt.gensalt(10)), rol);
    }

    public boolean hayUsuarios() throws SQLException {
        return usuarioDAO.hayUsuarios();
    }

    // ---------------- Gestión de usuarios ----------------

    /** Devuelve el motivo si la contraseña no cumple la política, o null si es válida. */
    public static String validarContrasena(String clave) {
        if (clave == null || clave.length() < LARGO_MINIMO_CLAVE) {
            return "La contraseña debe tener al menos " + LARGO_MINIMO_CLAVE + " caracteres.";
        }
        return null;
    }

    public List<UsuarioResumen> listarUsuarios() throws SQLException {
        return usuarioDAO.listar();
    }

    public void registrarUsuario(String nombre, String correo, String contrasena, String rol)
            throws SQLException, ReglaNegocioException {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException("Ingrese el nombre del usuario.");
        }
        if (!Validador.esCorreoValido(correo)) {
            throw new ReglaNegocioException("Ingrese un correo electrónico válido.");
        }
        String error = validarContrasena(contrasena);
        if (error != null) {
            throw new ReglaNegocioException(error);
        }
        try {
            usuarioDAO.crear(nombre.trim(), correo.trim(), BCrypt.hashpw(contrasena, BCrypt.gensalt(10)), rol);
        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState())) { // violación de UNIQUE
                throw new ReglaNegocioException("Ya existe un usuario con el correo " + correo.trim() + ".");
            }
            throw e;
        }
    }

    /** El administrador asigna una contraseña nueva a otro usuario. */
    public void restablecerContrasena(UsuarioResumen usuario, String nueva)
            throws SQLException, ReglaNegocioException {
        String error = validarContrasena(nueva);
        if (error != null) {
            throw new ReglaNegocioException(error);
        }
        usuarioDAO.actualizarContrasena(usuario.idUsuario(), BCrypt.hashpw(nueva, BCrypt.gensalt(10)));
    }

    /** No se permite cambiarse el rol a uno mismo (evita quedarse sin administradores). */
    public void cambiarRol(UsuarioResumen usuario, String nuevoRol, String correoQuienCambia)
            throws SQLException, ReglaNegocioException {
        if (usuario.correo().equalsIgnoreCase(correoQuienCambia)) {
            throw new ReglaNegocioException("No puede cambiar su propio rol.");
        }
        usuarioDAO.actualizarRol(usuario.idUsuario(), nuevoRol);
    }

    /** Cada usuario cambia su propia contraseña (debe conocer la actual). */
    public void cambiarMiContrasena(String correo, String actual, String nueva)
            throws SQLException, ReglaNegocioException {
        if (autenticar(correo, actual).isEmpty()) {
            throw new ReglaNegocioException("La contraseña actual no es correcta.");
        }
        String error = validarContrasena(nueva);
        if (error != null) {
            throw new ReglaNegocioException(error);
        }
        if (nueva.equals(actual)) {
            throw new ReglaNegocioException("La contraseña nueva debe ser distinta de la actual.");
        }
        usuarioDAO.actualizarContrasenaPorCorreo(correo, BCrypt.hashpw(nueva, BCrypt.gensalt(10)));
    }
}
