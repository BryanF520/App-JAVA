package com.example.gatekeeper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.gatekeeper.entities.Persona;
import com.example.gatekeeper.entities.Rol;
import com.example.gatekeeper.entities.Tipo_doc;
import com.example.gatekeeper.service.PersonaService;
import com.example.gatekeeper.service.RolService;
import com.example.gatekeeper.service.Tipo_docService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Collection;


@Controller
@RequestMapping("ingresos/personas")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private RolService rolService;

    @Autowired
    private Tipo_docService tipoDocService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long getRolIdFromAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("Usuaurio no autenticado");
        }

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        for(GrantedAuthority authority : authorities) {
            String rolNombre = authority.getAuthority(); //Rol ADMIN

            String rolNombreSinPrefijo = rolNombre.startsWith("ROLE_") ? rolNombre.substring(5) : rolNombre;

            Rol rol = rolService.obtenerPorRol(rolNombreSinPrefijo);
            if (rol != null ) {
                return rol.getId();
            }
        }

        throw new RuntimeException("Rol no encontrado");
    }


    @GetMapping
    public String index(Model model){
        List<Persona> personas = personaService.listarPersonas();
        model.addAttribute("personas", personas);
        return "ingresos/personas/index";
    }

    @GetMapping("/create")
    public String create(Model model, Authentication auth){
        /* Long userRolId = getRolIdFromAuth(auth);
        List<Rol> roles;
        if (userRolId == 1L) {
            roles = rolService.listarRol();
        } else if (userRolId == 2L) {
            roles = rolService.obtenerRolPorId(List.of(3L, 4L));
        } else {
            throw new RuntimeException("No tienes permisos para acceder.");
        } */

         List<Rol> roles = rolService.obtenerRolPorId(List.of(1L, 2L)); // Solo Admin y Recepcionista
        List<Tipo_doc> tipo_doc = tipoDocService.listarTipo_doc();
        model.addAttribute("roles", roles);
        model.addAttribute("tipo_doc", tipo_doc);
        model.addAttribute("persona", new Persona());
        return "ingresos/personas/create";
    }

    @PostMapping
    public String store(@ModelAttribute @Valid Persona persona, Authentication auth, Model model) {
        Long userRolId = getRolIdFromAuth(auth);

        // Solo el administrador (1L) puede asignar roles; recepcionista solo 3 y 4
        if (userRolId == 2L && !List.of(3L, 4L).contains(persona.getRol().getId())) {
        model.addAttribute("error", "No tienes permiso para asignar este rol.");
        return "ingresos/personas/create";
        }

        // Validación extra: solo permitir rol 1 (admin) o 2 (recepcionista)
        if (!List.of(1L, 2L).contains(persona.getRol().getId())) {
        model.addAttribute("error", "Solo puedes asignar roles de Administrador o Recepcionista.");
        return "ingresos/personas/create";
        }

        // Encriptar la contraseña si fue ingresada
        if (persona.getPassword() != null && !persona.getPassword().isBlank()) {
        persona.setPassword(passwordEncoder.encode(persona.getPassword()));
        }

        personaService.crearPersona(persona);
        return "redirect:/ingresos/personas";
    }


    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Persona persona = personaService.obtenerPersona(id);
        model.addAttribute("persona", persona);
        return "ingresos/personas/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Persona persona = personaService.obtenerPersona(id);
        List<Rol> roles = rolService.listarRol();
        List<Tipo_doc> tipodocs = tipoDocService.listarTipo_doc();

        model.addAttribute("persona", persona);
        model.addAttribute("roles", roles);
        model.addAttribute("tipodocs", tipodocs);
        return "ingresos/personas/edit";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute Persona persona, Model model) {
        if (persona.getPassword() != null && !persona.getPassword().isBlank()) {
            persona.setPassword(passwordEncoder.encode(persona.getPassword()));
        } else {
            Persona actual = personaService.obtenerPersona(id);
            persona.setPassword(actual.getPassword());
        }

        Persona actualizada = personaService.actualizarPersona(id, persona);
        if (actualizada == null) {
            model.addAttribute("error", "Error al actualizar la persona.");
            return "ingresos/personas/edit";
        }

        return "redirect:/ingresos/personas";
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id) {
        personaService.desactivarPersona(id);
        return "redirect:/ingresos/personas";
    }


}