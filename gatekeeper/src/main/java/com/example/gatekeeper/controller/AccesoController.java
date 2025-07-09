package com.example.gatekeeper.controller;

import com.example.gatekeeper.entities.Acceso;
import com.example.gatekeeper.entities.Persona;
import com.example.gatekeeper.entities.Tarjeta;
import com.example.gatekeeper.entities.Rol;
import com.example.gatekeeper.entities.Empresa;
import com.example.gatekeeper.service.AccesoService;
import com.example.gatekeeper.service.PersonaService;
import com.example.gatekeeper.service.TarjetaService;
import com.example.gatekeeper.service.RolService;
import com.example.gatekeeper.service.EmpresaService;

import jakarta.validation.Valid;//esto es para validar los campos de la empresa presuntamente

import org.springframework.beans.factory.annotation.Autowired;//esto inyecta dependencias
import org.springframework.security.core.Authentication;//usuario autenticado
import org.springframework.security.core.GrantedAuthority;//ni idea
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/accesos")
public class AccesoController {

    @Autowired
    private AccesoService accesoService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private RolService rolService;

    @Autowired
    private EmpresaService empresaService;

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

    @GetMapping
    public String index(Model model) {
        List<Acceso> accesos = accesoService.listarAcceso();
        model.addAttribute("accesos", accesos);
        return "ingresos/accesos/index";
    }

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

    @PostMapping
    public String store(@ModelAttribute @Valid Acceso acceso, @RequestParam("empresa_id") Long enpresaId) {
        Empresa empresa = empresaService.obtenerEmpresa(enpresaId);
        acceso.setEmpresa(empresa);

        accesoService.crearAcceso(acceso);
        return "redirect:/accesos";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Acceso acceso = accesoService.obtenerAcceso(id);
        model.addAttribute("acceso", acceso);
        return "accesos/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Acceso acceso = accesoService.obtenerAcceso(id);
        List<Persona> personas = personaService.listarPersonas();

        model.addAttribute("acceso", acceso);
        model.addAttribute("personas", personas);
        return "accesos/edit";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute Acceso acceso) {
        Acceso actualizado = accesoService.actualizarAcceso(id, acceso);
        if (actualizado == null) {
            return "accesos/edit";
        }
        return "redirect:/accesos";
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id) {
        accesoService.desactivarAcceso(id);
        return "redirect:/accesos";
    }
}