package com.example.gatekeeper.controller;

import com.example.gatekeeper.entities.Rol;
import com.example.gatekeeper.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/roles")
public class RolController {

    private final RolService rolService;

    @Autowired
    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("roles", rolService.listarRol());
        return "index"; // Vista index.html o index.jsp
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("rol", new Rol());
        return "roles/create"; // Vista roles/create.html
    }

    @PostMapping
    public String store(@Valid @ModelAttribute Rol rol, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "roles/create";
        }

        rolService.crearRol(rol);
        model.addAttribute("success", "Rol creado correctamente");
        return "redirect:/roles";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Rol rol = rolService.obtenerRol(id);
        model.addAttribute("rol", rol);
        return "roles/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Rol rol = rolService.obtenerRol(id);
        model.addAttribute("rol", rol);
        return "roles/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Rol rol, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "roles/edit";
        }

        rolService.actualizarRol(id, rol);
        model.addAttribute("success", "Rol actualizado correctamente");
        return "redirect:/roles";
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id, Model model) {
        rolService.desactivarRol(id);
        model.addAttribute("success", "Rol desactivado correctamente");
        return "redirect:/roles";
    }
}
