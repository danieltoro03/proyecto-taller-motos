package com.tallermotosv1.datos;

import com.tallermotosv1.enums.EstadoCita;
import com.tallermotosv1.enums.RolUsuario;
import com.tallermotosv1.modelo.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDatos {
    public static List<Usuario> usuarios = new ArrayList<>();
    public static List<Cita> citas = new ArrayList<>();

    static {
        // Usuarios para el Login
        usuarios.add(new Usuario("admin@taller.com", "1234", RolUsuario.ADMINISTRADOR));
        usuarios.add(new Usuario("mecanico@taller.com", "1234", RolUsuario.MECANICO));

        // Datos de prueba (Mock) para que la tabla no esté vacía al iniciar
        Cliente c1 = new Cliente(1, "10203040", "Carlos Perez", "3001234567", "carlos@email.com");
        Moto m1 = new Moto(1, "ABC12D", "Yamaha", "FZ", 150, "Cambios", c1);
        citas.add(new Cita(1, LocalDateTime.now().plusDays(1), EstadoCita.PENDIENTE, "Mantenimiento general", m1, usuarios.get(0)));
        
        Cliente c2 = new Cliente(2, "90807060", "Maria Gomez", "3109876543", "maria@email.com");
        Moto m2 = new Moto(2, "XYZ98F", "Honda", "CB", 125, "Automática", c2);
        citas.add(new Cita(2, LocalDateTime.now().plusDays(1), EstadoCita.EN_PROCESO, "Cambio de aceite", m2, usuarios.get(0)));
    }
}