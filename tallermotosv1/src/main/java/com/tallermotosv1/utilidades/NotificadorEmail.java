package com.tallermotosv1.utilidades;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class NotificadorEmail {
    
    public static void enviarCorreo(String destinatario, String asunto, String mensajeTexto) {
        final String remitente = "danielricardotoroibarguen@gmail.com"; 
        final String password = "xravmxxrpcqzqllr"; 

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(remitente));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setText(mensajeTexto);
            Transport.send(message);
            System.out.println("Correo enviado a: " + destinatario); 
        } catch (MessagingException e) {
            System.out.println("Error al enviar correo: " + e.getMessage());
        }
    }
}