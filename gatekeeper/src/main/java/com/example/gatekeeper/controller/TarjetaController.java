package com.example.gatekeeper.controller;

import com.example.gatekeeper.entities.Tarjeta;
import com.example.gatekeeper.service.TarjetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tarjetas")
public class TarjetaController {

    private final TarjetaService tarjetaService;

    @Autowired
    public TarjetaController(TarjetaService tarjetaService) {
        this.tarjetaService = tarjetaService;
    }

    @GetMapping
    public String index(Model model) {
        // Aquí iría la lógica para listar tarjetas
        // model.addAttribute("tarjetas", tarjetaService.listarTarjetas());
        return "tarjetas/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("tarjeta", new Tarjeta());
        return "tarjetas/create";
    }

    @PostMapping
    public String store(@ModelAttribute Tarjeta tarjeta) {
        // tarjetaService.crearTarjeta(tarjeta);
        return "redirect:/tarjetas";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        // Tarjeta tarjeta = tarjetaService.obtenerTarjeta(id);
        // model.addAttribute("tarjeta", tarjeta);
        return "tarjetas/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        // Tarjeta tarjeta = tarjetaService.obtenerTarjeta(id);
        // model.addAttribute("tarjeta", tarjeta);
        return "tarjetas/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Tarjeta tarjeta) {
        // tarjetaService.actualizarTarjeta(id, tarjeta);
        return "redirect:/tarjetas";
    }

    @PostMapping("/{id}/delete")
    public String destroy(@PathVariable Long id) {
        // tarjetaService.eliminarTarjeta(id);
        return "redirect:/tarjetas";
    }
}
