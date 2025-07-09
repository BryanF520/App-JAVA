package com.example.gatekeeper.controller;

import com.example.gatekeeper.entities.Acceso;
import com.example.gatekeeper.entities.Persona;
import com.example.gatekeeper.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/ingresos")
public class IngresoController {

    @Autowired
    private AccesoService accesoService;

    @Autowired
    private PersonaService personaService;

    @Autowired
    private Tipo_docService tipoDocService;

    @Autowired
    private RolService rolService;

    @Autowired
    private EmpresaService empresaService;

    // LISTADO
    @GetMapping
    public String index(Model model) {
        List<Acceso> ingresos = accesoService.listarAcceso();
        model.addAttribute("ingresos", ingresos);
        return "ingresos/index";
    }

    @GetMapping("/personas/index")
    public String vistaDetalladaPersonasDesdeIngresos(Model model) {
        List<Persona> personas = personaService.listarPersonas();
        model.addAttribute("personas", personas);
        return "ingresos/personas/index";
    }

    @GetMapping("/accesos/index")
    public String vistaDetalladaAccesosDesdeIngresos(Model model) {
        List<Acceso> accesos = accesoService.listarAcceso(); // o findAll()
        model.addAttribute("accesos", accesos);
        return "ingresos/accesos/index";
    }   


    // CREAR NUEVO INGRESO 
    @GetMapping("/create")
    public String create(Model model) {
        Acceso nuevoAcceso = new Acceso();
        nuevoAcceso.setPersona(new Persona());
        nuevoAcceso.setFecha_ingreso(LocalDateTime.now());
        model.addAttribute("ingreso", nuevoAcceso);
        model.addAttribute("tipodocs", tipoDocService.listarTipo_doc());
        model.addAttribute("empresas", empresaService.listarEmpresa());
        model.addAttribute("roles", rolService.obtenerRolPorId(List.of(3L, 4L)));
        return "ingresos/create";
    }

    // GUARDAR INGRESO
    @PostMapping
    public String store(@ModelAttribute("ingreso") Acceso acceso, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Error al guardar el ingreso");
            return "ingresos/create";
        }

        if (acceso.getPersona() == null) {
        model.addAttribute("error", "Faltan los datos de la persona");
        return "ingresos/create";
        }

        Optional<Persona> personaExistente = personaService.buscarPorDocOTelefono(
                acceso.getPersona().getNumDoc(), acceso.getPersona().getTelefono()
        );

        Persona persona = personaExistente.orElseGet(() -> {
            Persona nueva = acceso.getPersona();
            nueva.setRol(rolService.obtenerRol(4L));
            return personaService.crearPersona(nueva);
        });

        acceso.setPersona(persona);
        if (acceso.getFecha_ingreso() == null) {
            acceso.setFecha_ingreso(LocalDateTime.now());
        }

        accesoService.crearAcceso(acceso);
        return "redirect:/ingresos";
    }

    // EDITAR INGRESO
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Acceso ingreso = accesoService.obtenerAcceso(id);
        model.addAttribute("ingreso", ingreso);
        model.addAttribute("tipodocs", tipoDocService.listarTipo_doc());
        model.addAttribute("empresas", empresaService.listarEmpresa());
        model.addAttribute("roles", rolService.obtenerRolPorId(List.of(3L, 4L)));
        return "ingresos/edit";
    }

    // ACTUALIZAR INGRESO
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("ingreso") Acceso accesoActualizado, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "ingresos/edit";
        }

        Acceso ingreso = accesoService.obtenerAcceso(id);
        Persona persona = ingreso.getPersona();

        persona.setTipoDoc(accesoActualizado.getPersona().getTipoDoc());
        persona.setNumDoc(accesoActualizado.getPersona().getNumDoc());
        persona.setNombreUno(accesoActualizado.getPersona().getNombreUno());
        persona.setNombreDos(accesoActualizado.getPersona().getNombreDos());
        persona.setApellidoUno(accesoActualizado.getPersona().getApellidoUno());
        persona.setApellidoDos(accesoActualizado.getPersona().getApellidoDos());
        persona.setTelefono(accesoActualizado.getPersona().getTelefono());

        personaService.actualizarPersona(persona.getId(), persona);

        ingreso.setMotivo(accesoActualizado.getMotivo());
        ingreso.setFecha_ingreso(
                accesoActualizado.getFecha_ingreso() != null
                        ? accesoActualizado.getFecha_ingreso()
                        : LocalDateTime.now()
        );

        ingreso.setEmpresa(accesoActualizado.getEmpresa());
        accesoService.actualizarAcceso(id, ingreso);
        return "redirect:/ingresos";
    }

    // MOSTRAR INGRESO DETALLADO
    @GetMapping("/show/{id}")
    public String mostrarIngreso(@PathVariable Long id, Model model) {
        Acceso acceso = accesoService.obtenerAcceso(id);
        if (acceso == null) {
        return "redirect:/ingresos?error=Ingreso no encontrado";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String fechaFormateada = acceso.getFecha_ingreso().format(formatter);

        model.addAttribute("ingreso", acceso);
        model.addAttribute("fechaFormateada", fechaFormateada);
        return "ingresos/show";
    }

    // ELIMINAR INGRESO
    @PostMapping("/delete/{id}")
    public String destroy(@PathVariable Long id) {
        Acceso acceso = accesoService.obtenerAcceso(id);
        if (acceso != null && acceso.getPersona() != null) {
            Persona persona = acceso.getPersona();
            accesoService.desactivarAcceso(acceso.getId());
            personaService.desactivarPersona(persona.getId());
        }
        return "redirect:/ingresos";
    }

    /* // MOSTRAR DETALLE 
    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Acceso ingreso = accesoService.obtenerAcceso(id);
        model.addAttribute("ingreso", ingreso);
        return "ingresos/show";
    } */
}
