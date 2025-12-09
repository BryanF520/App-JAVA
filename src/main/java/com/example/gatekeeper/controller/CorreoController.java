package com.example.gatekeeper.controller;

import com.example.gatekeeper.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class CorreoController {

    @Autowired
    private EmailService emailService;

    //Muestra el formulario para redactar un correo
    @GetMapping("/correo/redactarCorreo")
    public String mostrarFormularioCorreo() {
        return "correo/redactarCorreo";
    }

    //Procesa el envío del correo
    @PostMapping("/correo/enviar")
    public String enviarCorreo(
        @RequestParam String destinatario, 
        @RequestParam String asunto,
        @RequestParam String mensaje,
        @RequestParam("adjunto") MultipartFile adjunto,
        Model model) {

    // Separar los correos por coma
    String[] listaDestinatarios = destinatario.split(",");

    // Expresión regular para validar email
    String regexEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    // Validar cada correo
    for (String correo : listaDestinatarios) {
        correo = correo.trim(); // quitar espacios

        if (!correo.matches(regexEmail)) {
            model.addAttribute("mensaje", "Correo inválido: " + correo);
            return "correo/redactarCorreo";
        }
    }

    try {
        // Enviar el correo
        emailService.enviarCorreoConAdjuntoMultiple(destinatario, asunto, mensaje, adjunto);

        model.addAttribute("success", "Correo enviado correctamente.");
        return "correo/redactarCorreo";

    } catch (Exception e) {
        model.addAttribute("mensaje", "Error al enviar: " + e.getMessage());
        return "correo/redactarCorreo";
    }
}

}