package com.example.gatekeeper.controller;

import com.example.gatekeeper.Dao.PersonaDao;
import com.example.gatekeeper.entities.Persona;
import com.example.gatekeeper.entities.Rol;
import com.example.gatekeeper.entities.Tipo_doc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CargaCSVPersonaController {

    @Autowired
    private PersonaDao personaDao;

    @PostMapping("/cargarPersona")
public String cargarPersona(@RequestParam("file") MultipartFile file, RedirectAttributes redirect) {
    List<Persona> nuevas = new ArrayList<>();
    int duplicado = 0;

    try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
        String linea;
        boolean primera = true;

        while ((linea = br.readLine()) != null) {

            if (primera) {
                primera = false;
                continue;
            }

            if (linea.trim().isEmpty()) continue;

            String[] datos = linea.split(";");
            if (datos.length < 8) continue;

            Long tipoDocId = Long.parseLong(datos[0]);
            String numDoc = datos[1];
            String nombreUno = datos[2];
            String nombreDos = datos[3];
            String apellidoUno = datos[4];
            String apellidoDos = datos[5];
            String telefono = datos[6];
            Long rolId = Long.parseLong(datos[7]);

            // Verificar duplicados
            if (personaDao.existsByNumDoc(numDoc)) {
                duplicado++;
                continue;
            }

            // Crear persona
            Persona persona = new Persona();
            persona.setNumDoc(numDoc);
            persona.setNombreUno(nombreUno);
            persona.setNombreDos(nombreDos);
            persona.setApellidoUno(apellidoUno);
            persona.setApellidoDos(apellidoDos);
            persona.setTelefono(telefono);

            // Asignar tipo documento desde CSV
            Tipo_doc tipoDoc = new Tipo_doc();
            tipoDoc.setId(tipoDocId);
            persona.setTipoDoc(tipoDoc);

            // Asignar rol desde CSV
            Rol rol = new Rol();
            rol.setId(rolId);
            persona.setRol(rol);

            nuevas.add(persona);
        }

        // Guardar todas
        personaDao.saveAll(nuevas);

        // Mensaje flash
        redirect.addFlashAttribute("mensaje",
                "Carga completada: " + nuevas.size() + " nuevas, " + duplicado + " duplicados.");

        // REDIRECT 
        return "redirect:/ingresos/personas/index";

    } catch (Exception e) {
        e.printStackTrace();
        redirect.addFlashAttribute("mensaje", "Error al procesar el archivo: " + e.getMessage());
        return "redirect:/ingresos/personas/index";
    }
}
}
