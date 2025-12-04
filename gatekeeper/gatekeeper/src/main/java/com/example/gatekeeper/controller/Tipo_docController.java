package com.example.gatekeeper.controller;

import com.example.gatekeeper.entities.Tipo_doc;
import com.example.gatekeeper.service.Tipo_docService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;



@Controller
@RequestMapping("/tipodocs")
public class Tipo_docController {

    private final Tipo_docService tipodocService;

    @Autowired
    public Tipo_docController(Tipo_docService tipodocService) {
        this.tipodocService = tipodocService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("tipodocs", tipodocService.listarTipo_doc());
        return "tipodocs/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("tipodoc", new Tipo_doc());
        return "tipodocs/create";
    }

    @PostMapping
    public String store(@Valid @ModelAttribute Tipo_doc tipodoc, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "tipodocs/create";
        }

        tipodocService.crearTipo_doc(tipodoc);
        model.addAttribute("success", "Tipo de documento creado correctamente");
        return "redirect:/tipodocs";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Tipo_doc tipodoc = tipodocService.obtenerTipo_doc(id);
        model.addAttribute("tipodoc", tipodoc);
        return "tipodocs/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        Tipo_doc tipodoc = tipodocService.obtenerTipo_doc(id);
        model.addAttribute("tipodoc", tipodoc);
        return "tipodocs/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute Tipo_doc tipodoc, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "tipodocs/edit";
        }

        tipodocService.actualizarTipo_doc(id, tipodoc);
        model.addAttribute("success", "Tipo de documento actualizado correctamente");
        return "redirect:/tipodocs";
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id, Model model) {
        tipodocService.desactivarTipo_doc(id);;
        model.addAttribute("success", "Tipo de documento desactivado correctamente");
        return "redirect:/tipodocs";
    }
}
