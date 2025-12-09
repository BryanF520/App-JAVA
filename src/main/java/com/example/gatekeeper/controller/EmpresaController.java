package com.example.gatekeeper.controller;
import com.example.gatekeeper.entities.Empresa;
import com.example.gatekeeper.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/inactivos")
    public String listarInactivos(Model model) {
        model.addAttribute("empresas", empresaService.listarInactivos());
        return "empresas/inactivos";
    }

    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String busqueda, Model model) {
        List<Empresa> empresas;
        
        // Si hay una búsqueda, filtrar en todos los campos
        if (busqueda != null && !busqueda.isEmpty()) {
            empresas = empresaService.buscarPorTermino(busqueda);
        } else {
            // Si no hay búsqueda, mostrar todas
            empresas = empresaService.listarEmpresa();
        }
        
        model.addAttribute("empresas", empresas);
        model.addAttribute("busqueda", busqueda);
        return "empresas/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("empresa", new Empresa());
        return "empresas/create";
    }

    @PostMapping
    public String store(@ModelAttribute @Valid Empresa empresa, BindingResult result, Model model) {
        // Validar si el NIT ya existe
        if (empresaService.existeNit(empresa.getNit())) {
            model.addAttribute("empresa", empresa);
            model.addAttribute("error", "El NIT de la empresa ya esta registrado.");
            return "empresas/create"; // vuelve al formulario para que no se agrege
        // Validar si el nombre ya existe
        } else if(empresaService.existeNombre(empresa.getNombre())) {
           model.addAttribute("empresa", empresa);
           model.addAttribute("error", "El Nombre de la empresa ya está registrado."); 
           return "empresas/create"; // vuelve al formulario para que no se agrege
        //Validar campo contacto
        } else if (result.hasErrors()) {
            model.addAttribute("empresa", empresa);
            model.addAttribute("errors", result.getAllErrors());
            return "empresas/create"; // vuelve al formulario para que no se agrege
        }
        
        empresaService.crearEmpresa(empresa);
        return "redirect:/empresas"; // redirige a la lista de empresas solo si el NIT es único
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
    public String update(@PathVariable Long id,@Valid @ModelAttribute Empresa empresa, BindingResult result, Model model) {
        
        // Validar si el NIT ya existe
        if (empresaService.existeNitEditar(id, empresa.getNit())) {
            model.addAttribute("empresa", empresa);
            model.addAttribute("error", "El NIT de la empresa ya esta registrado.");
            return "empresas/edit"; // vuelve al formulario para que no se agrege
        // Validar si el nombre ya existe
        } else if(empresaService.existeNombreEditar(id, empresa.getNombre())) {
           model.addAttribute("empresa", empresa);
           model.addAttribute("error", "El Nombre de la empresa ya está registrado."); 
           return "empresas/edit"; // vuelve al formulario para que no se agrege
        //Validar campo contacto
        } else if (result.hasErrors()) {
            model.addAttribute("empresa", empresa);
            model.addAttribute("errors", result.getAllErrors());
            return "empresas/edit"; // vuelve al formulario para que no se agrege
        }
        Empresa actualizada = empresaService.actualizarEmpresa(id, empresa);
        if (actualizada == null) {
            model.addAttribute("error", "No se pudo actualizar la empresa.");
            return "empresas/edit";
        }
        return "redirect:/empresas";
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id, Model model) {
        try {
            empresaService.desactivarEmpresa(id);
            return "redirect:/empresas";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            List<Empresa> empresas = empresaService.listarEmpresa();
            model.addAttribute("empresas", empresas);
            return "empresas/index"; // Vuelve al listado mostrando el error
        }
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable Long id, RedirectAttributes redir) {
         try {
        empresaService.activarEmpresa(id);
        redir.addFlashAttribute("success", "Empresa activada correctamente.");
    } catch (Exception e) {
        redir.addFlashAttribute("error", e.getMessage());
    }
    return "redirect:/empresas/inactivos";
    }

}