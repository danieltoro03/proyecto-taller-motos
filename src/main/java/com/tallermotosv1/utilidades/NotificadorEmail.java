package com.tallermotosv1.utilidades;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.SwingUtilities;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Envío de correos. Las credenciales se leen de config.properties
 * (mail.host, mail.port, mail.user, mail.password); NO van en el código.
 */
public final class NotificadorEmail {

    private static final ExecutorService EJECUTOR = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "notificador-email");
        t.setDaemon(true);
        return t;
    });

    private NotificadorEmail() {
    }

    /** Envío síncrono (bloquea). No llamar desde el hilo de la interfaz. */
    public static void enviarCorreo(String destinatario, String asunto, String mensajeTexto) throws MessagingException {
        final String remitente = Configuracion.get("mail.user");
        final String password = Configuracion.get("mail.password");
        if (remitente == null || remitente.isEmpty() || password == null || password.isEmpty()
                || password.startsWith("tu_")) {
            throw new MessagingException("Configura mail.user y mail.password en config.properties.");
        }

        String host = Configuracion.get("mail.smtp.host", "smtp.gmail.com");
        String puerto = Configuracion.get("mail.smtp.port", "587");

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", puerto);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.trust", host);
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        if ("465".equals(puerto)) {
            props.put("mail.smtp.ssl.enable", "true");
        } else {
            props.put("mail.smtp.starttls.enable", "true");
        }

        // getInstance (no getDefaultInstance, que reutiliza la primera sesión creada)
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(remitente));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject(asunto);
        message.setText(mensajeTexto);
        Transport.send(message);
    }

    /**
     * Envío en segundo plano para no congelar la ventana.
     * Si falla, llama a onError en el hilo de Swing con el motivo.
     */
    public static void enviarCorreoAsync(String destinatario, String asunto, String mensajeTexto,
                                         Consumer<String> onError) {
        enviarCorreoAsync(destinatario, asunto, mensajeTexto, null, onError);
    }

    /** Variante que informa el resultado para persistirlo en Notificacion. */
    public static void enviarCorreoAsync(String destinatario, String asunto, String mensajeTexto,
                                         Runnable onSuccess, Consumer<String> onError) {
        EJECUTOR.submit(() -> {
            try {
                enviarCorreo(destinatario, asunto, mensajeTexto);
                if (onSuccess != null) SwingUtilities.invokeLater(onSuccess);
            } catch (Exception e) {
                String motivo = e.getMessage() == null ? e.toString() : e.getMessage();
                System.err.println("Error al enviar correo a " + destinatario + ": " + motivo);
                if (onError != null) SwingUtilities.invokeLater(() -> onError.accept(motivo));
            }
        });
    }
}
