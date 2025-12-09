package com.example.gatekeeper.service;

import com.example.gatekeeper.entities.Empresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoSimple(String para, String asunto, String texto) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(para);
        mensaje.setSubject(asunto);
        mensaje.setText(texto);
        mailSender.send(mensaje);
    }

/*     public void enviarCorreosMasivos(List<Empresa> empresas, String asunto, String textoBase) {
        for (Empresa empresa : empresas) {
            if (empresa.getCorreo() != null && !empresa.getCorreo().isBlank()) {
                String textoPersonalizado = textoBase
                        .replace("{nombre}", empresa.getNombre() != null ? empresa.getNombre() : "")
                        .replace("{area}", empresa.getArea() != null ? empresa.getArea() : "");
                enviarCorreoSimple(empresa.getCorreo(), asunto, textoPersonalizado);

                try {
                    Thread.sleep(1200); // 1.2 segundos entre correos
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
 */
    public void enviarCorreoConAdjunto(String destinatario, String asunto, String mensaje, MultipartFile rutaArchivo) 
            throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(mensaje, true);

        if (!rutaArchivo.isEmpty()) {
        helper.addAttachment(rutaArchivo.getOriginalFilename(), rutaArchivo);
    }

        mailSender.send(mimeMessage);
    }

    public void enviarCorreoConAdjuntoMultiple(String destinatarios, String asunto, String mensaje, MultipartFile adjunto)
        throws MessagingException, IOException {

    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    // separa por coma
    String[] lista = destinatarios.split(",");

    helper.setTo(lista);
    helper.setSubject(asunto);
    helper.setText(mensaje, true);

    if (adjunto != null && !adjunto.isEmpty()) {
        helper.addAttachment(adjunto.getOriginalFilename(), adjunto);
    }

    mailSender.send(message);
}
}