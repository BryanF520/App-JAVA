package com.example.gatekeeper.controller;

import com.example.gatekeeper.entities.Empresa;
import com.example.gatekeeper.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    @GetMapping
    public String index(Model model) {
        List<Empresa> empresas = empresaService.listarEmpresa();
        model.addAttribute("empresas", empresas);
        return "empresas/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("empresa", new Empresa());
        return "empresas/create";
    }

    @PostMapping
    public String store(@ModelAttribute @Valid Empresa empresa) {
        empresaService.crearEmpresa(empresa);
        return "redirect:/empresas";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Empresa empresa = empresaService.obtenerEmpresa(id);
        model.addAttribute("empresa", empresa);
        return "empresas/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Empresa empresa = empresaService.obtenerEmpresa(id);
        model.addAttribute("empresa", empresa);
        return "empresas/edit";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute Empresa empresa, Model model) {
        Empresa actualizada = empresaService.actualizarEmpresa(id, empresa);
        if (actualizada == null) {
            model.addAttribute("error", "No se pudo actualizar la empresa.");
            return "empresas/edit";
        }
        return "redirect:/empresas";
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id) {
        empresaService.desactivarEmpresa(id);
        return "redirect:/empresas";
    }
}