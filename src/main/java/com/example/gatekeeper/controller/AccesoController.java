package com.example.gatekeeper.controller;

import com.example.gatekeeper.entities.Acceso;
import com.example.gatekeeper.entities.Persona;

import com.example.gatekeeper.entities.Rol;
import com.example.gatekeeper.entities.Empresa;
import com.example.gatekeeper.service.AccesoService;
import com.example.gatekeeper.service.PersonaService;

import com.example.gatekeeper.service.RolService;
import com.example.gatekeeper.service.EmpresaService;

import jakarta.validation.Valid;//esto es para validar los campos de la empresa presuntamente

import org.springframework.beans.factory.annotation.Autowired;//esto inyecta dependencias
import org.springframework.security.core.Authentication;//usuario autenticado
import org.springframework.security.core.GrantedAuthority;//ni idea
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("ingresos/accesos")
public class AccesoController {

    // Inyección de dependencias: servicios necesarios para este controlador
    @Autowired
    private AccesoService accesoService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private RolService rolService;

    @Autowired
    private EmpresaService empresaService;

    //     Método privado que obtiene el ID del rol del usuario autenticado
    private Long getRolIdFromAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("Usuario no autenticado");
        }

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            String rolNombre = authority.getAuthority();
            String rolNombreSinPrefijo = rolNombre.startsWith("ROLE_") ? rolNombre.substring(5) : rolNombre;
            Rol rol = rolService.obtenerPorRol(rolNombreSinPrefijo);
            if (rol != null) {
                return rol.getId();
            }
        }
        throw new RuntimeException("Rol no encontrado");
    }

    //Lista todos los accesos registrados
    @GetMapping
    public String index(Model model, 
            @RequestParam(required = false) String nombrePersona,
            @RequestParam(required = false) String empresa) {
        List<Acceso> accesos;
        if ((nombrePersona != null && !nombrePersona.isEmpty()) || (empresa != null && !empresa.isEmpty())) {
            accesos = accesoService.listarAcceso().stream()
                    .filter(a -> {
                        boolean coincidePersona = nombrePersona == null || nombrePersona.isEmpty() || 
                            (a.getPersona() != null && a.getPersona().getNombreUno() != null && 
                             a.getPersona().getNombreUno().toLowerCase().contains(nombrePersona.toLowerCase()));
                        boolean coincideEmpresa = empresa == null || empresa.isEmpty() || 
                            (a.getEmpresa() != null && a.getEmpresa().getNombre() != null && 
                             a.getEmpresa().getNombre().toLowerCase().contains(empresa.toLowerCase()));
                        return coincidePersona && coincideEmpresa;
                    })
                    .toList();
            model.addAttribute("filtroNombrePersona", nombrePersona);
            model.addAttribute("filtroEmpresa", empresa);
        } else {
            accesos = accesoService.listarAcceso();
        }
        model.addAttribute("accesos", accesos);
        return "ingresos/accesos/index";
    }

    @GetMapping("/inactivos")
    public String listarInactivos (Model model) {
        model.addAttribute("accesos", accesoService.listarIncactivos());
        return "ingresos/accesos/inactivos";
    }

    // Muestra el formulario para crear un acceso
    @GetMapping("/create")
    public String create(Model model, Authentication auth) {
        Long userRolId = getRolIdFromAuth(auth);

        if (userRolId != 1L && userRolId != 2L) {
            throw new RuntimeException("No tienes permisos para acceder.");
        }

        List<Persona> personas = personaService.listarPersonas();
        List<Empresa> empresas = empresaService.listarEmpresa();

        model.addAttribute("personas", personas);
        model.addAttribute("empresas", empresas);
        model.addAttribute("acceso", new Acceso());
        return "accesos/create";
    }

    //Guarda un nuevo acceso en la base de datos
    @PostMapping
    public String store(@ModelAttribute @Valid Acceso acceso, @RequestParam("empresa_id") Long enpresaId) {
        Empresa empresa = empresaService.obtenerEmpresa(enpresaId);
        acceso.setEmpresa(empresa);

        accesoService.crearAcceso(acceso);
        return "redirect:/accesos";
    }

    //Muestra el detalle de un acceso específico
    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Acceso acceso = accesoService.obtenerAcceso(id);
        model.addAttribute("acceso", acceso);
        return "accesos/show";
    }

    //Muestra formulario para editar un acceso
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Acceso acceso = accesoService.obtenerAcceso(id);

        if (acceso == null) {
            return "redirect:/ingresos"; // seguridad básica
        }

        model.addAttribute("acceso", acceso);
        model.addAttribute("empresas", empresaService.listarEmpresa());
        return "ingresos/accesos/edit";
    }

    //ctualiza un acceso en la BD
    @PostMapping("/{id}/update")
    public String update(
        @PathVariable Long id,
        @ModelAttribute("acceso") Acceso nuevosDatos,
        RedirectAttributes redirectAttributes) {

        Acceso actualizado = accesoService.actualizarAcceso(id, nuevosDatos);

        if (actualizado == null) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el acceso.");
            return "redirect:/ingresos/accesos/" + id + "/edit";
        }

        redirectAttributes.addFlashAttribute("success", "Acceso actualizado correctamente.");
        return "redirect:/ingresos/accesos";
    }

    //Desactiva (elimina lógicamente) un acceso
    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id, Model model) {
        try {
            accesoService.desactivarAcceso(id);
            return "redirect:/ingresos/accesos";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            List<Acceso> accesos = accesoService.listarAcceso();
            model.addAttribute("accesos", accesos);
            return "ingresos/accesos/index"; // Vuelve al listado mostrando el error
        }
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable Long id, RedirectAttributes redir) {
        try {
            accesoService.activarAcceso(id);
            redir.addFlashAttribute("success", "Acceso activado perfectamente.");
        } catch (Exception e) {
            redir.addFlashAttribute("error", "No se pudo activar el acceso.");
            return "redirect:/ingresos/accesos/inactivos";
        }
        return "redirect:/ingresos/accesos/inactivos";
    }
}