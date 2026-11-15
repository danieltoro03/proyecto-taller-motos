package com.tallermotosv1.utilidades;

import com.tallermotosv1.excepciones.ReglaNegocioException;

import javax.swing.*;
import java.awt.Component;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Ejecuta una tarea (consulta a la BD, etc.) fuera del hilo de la interfaz y
 * muestra el error al usuario si falla. alExito se ejecuta en el hilo de Swing.
 */
public final class TareaSegundoPlano {

    private TareaSegundoPlano() {
    }

    public static <T> void ejecutar(Component padre, Callable<T> tarea, Consumer<T> alExito) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarea.call();
            }

            @Override
            protected void done() {
                try {
                    T resultado = get();
                    if (alExito != null) {
                        alExito.accept(resultado);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();
                    if (causa instanceof ReglaNegocioException) {
                        JOptionPane.showMessageDialog(padre, causa.getMessage(), "No se pudo completar",
                                JOptionPane.WARNING_MESSAGE);
                    } else if (causa instanceof SQLException) {
                        JOptionPane.showMessageDialog(padre, "Error de base de datos: " + causa.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(padre, "Error inesperado: " + causa,
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }.execute();
    }
}
