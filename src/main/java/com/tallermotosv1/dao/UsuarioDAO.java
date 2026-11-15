package com.tallermotosv1.dao;

import com.tallermotosv1.conexion.ConexionDB;
import com.tallermotosv1.dto.UsuarioResumen;
import com.tallermotosv1.dto.UsuarioSesion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {

    /** Devuelve el usuario con su HASH en el campo contrasena (no la clave en texto). */
    public Optional<UsuarioSesion> buscarPorCorreo(String correo) throws SQLException {
        String sql = "SELECT correo, contrasena, rol FROM Usuario WHERE correo = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new UsuarioSesion(
                            rs.getString("correo"),
                            rs.getString("contrasena"),
                            rs.getString("rol")));
                }
            }
        }
        return Optional.empty();
    }

    public boolean hayUsuarios() throws SQLException {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Usuario");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public void crear(String nombre, String correo, String hash, String rol) throws SQLException {
        String sql = "INSERT INTO Usuario (nombre_completo, correo, contrasena, rol) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.setString(3, hash);
            ps.setString(4, rol);
            ps.executeUpdate();
        }
    }

    public List<UsuarioResumen> listar() throws SQLException {
        String sql = "SELECT id_usuario, nombre_completo, correo, rol FROM Usuario ORDER BY nombre_completo";
        List<UsuarioResumen> lista = new ArrayList<>();
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new UsuarioResumen(rs.getInt("id_usuario"), rs.getString("nombre_completo"),
                        rs.getString("correo"), rs.getString("rol")));
            }
        }
        return lista;
    }

    public void actualizarContrasena(int idUsuario, String hash) throws SQLException {
        ejecutarUpdate("UPDATE Usuario SET contrasena = ? WHERE id_usuario = ?", hash, idUsuario);
    }

    public void actualizarContrasenaPorCorreo(String correo, String hash) throws SQLException {
        String sql = "UPDATE Usuario SET contrasena = ? WHERE correo = ?";
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setString(2, correo);
            ps.executeUpdate();
        }
    }

    public void actualizarRol(int idUsuario, String rol) throws SQLException {
        ejecutarUpdate("UPDATE Usuario SET rol = ? WHERE id_usuario = ?", rol, idUsuario);
    }

    private void ejecutarUpdate(String sql, String valor, int idUsuario) throws SQLException {
        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, valor);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }
}
