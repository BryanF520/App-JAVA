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
    public String index(Model model, @RequestParam(required = false) String fecha) {
        List<Acceso> ingresos;
        if (fecha != null && !fecha.isEmpty()) {
            ingresos = accesoService.listarAcceso().stream()
                    .filter(a -> a.getFecha_ingreso() != null && 
                           a.getFecha_ingreso().toLocalDate().toString().contains(fecha))
                    .toList();
            model.addAttribute("filtroFecha", fecha);
        } else {
            ingresos = accesoService.listarAcceso();
        }
        model.addAttribute("ingresos", ingresos);
        model.addAttribute("totalIngresos", ingresos.size());
        return "ingresos/index";
    }

    @GetMapping("/personas/index")
    public String vistaDetalladaPersonasDesdeIngresos(Model model, 
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String numDoc,
            @RequestParam(required = false) String busqueda) {
        List<Persona> personas;
        if (busqueda != null && !busqueda.isEmpty()) {
            String q = busqueda.toLowerCase();
            personas = personaService.listarPersonas().stream()
                    .filter(p -> {
                        return (p.getNombreUno() != null && p.getNombreUno().toLowerCase().contains(q)) ||
                               (p.getNombreDos() != null && p.getNombreDos().toLowerCase().contains(q)) ||
                               (p.getApellidoUno() != null && p.getApellidoUno().toLowerCase().contains(q)) ||
                               (p.getApellidoDos() != null && p.getApellidoDos().toLowerCase().contains(q)) ||
                               (p.getNumDoc() != null && p.getNumDoc().toLowerCase().contains(q)) ||
                               (p.getTelefono() != null && p.getTelefono().toLowerCase().contains(q));
                    })
                    .toList();
            model.addAttribute("busqueda", busqueda);
        } else if ((nombre != null && !nombre.isEmpty()) || (numDoc != null && !numDoc.isEmpty())) {
            personas = personaService.listarPersonas().stream()
                    .filter(p -> {
                        boolean coincideNombre = nombre == null || nombre.isEmpty() || 
                            (p.getNombreUno() != null && p.getNombreUno().toLowerCase().contains(nombre.toLowerCase())) ||
                            (p.getApellidoUno() != null && p.getApellidoUno().toLowerCase().contains(nombre.toLowerCase()));
                        boolean coincideDoc = numDoc == null || numDoc.isEmpty() || 
                            (p.getNumDoc() != null && p.getNumDoc().contains(numDoc));
                        return coincideNombre && coincideDoc;
                    })
                    .toList();
            model.addAttribute("filtroNombre", nombre);
            model.addAttribute("filtroNumDoc", numDoc);
        } else {
            personas = personaService.listarPersonas();
        }
        model.addAttribute("personas", personas);
        model.addAttribute("totalPersonas", personas.size());
        return "ingresos/personas/index";
    }

    @GetMapping("/accesos/index")
    public String vistaDetalladaAccesosDesdeIngresos(Model model, 
            @RequestParam(required = false) String nombrePersona,
            @RequestParam(required = false) String empresa,
            @RequestParam(required = false) String busqueda) {
        List<Acceso> accesos;
        if (busqueda != null && !busqueda.isEmpty()) {
            String q = busqueda.toLowerCase();
            accesos = accesoService.listarAcceso().stream()
                    .filter(a -> {
                        boolean matchPersona = a.getPersona() != null && (
                            (a.getPersona().getNombreUno() != null && a.getPersona().getNombreUno().toLowerCase().contains(q)) ||
                            (a.getPersona().getApellidoUno() != null && a.getPersona().getApellidoUno().toLowerCase().contains(q)) ||
                            (a.getPersona().getNumDoc() != null && a.getPersona().getNumDoc().toLowerCase().contains(q))
                        );
                        boolean matchEmpresa = a.getEmpresa() != null && a.getEmpresa().getNombre() != null && a.getEmpresa().getNombre().toLowerCase().contains(q);
                        boolean matchMotivo = a.getMotivo() != null && a.getMotivo().toLowerCase().contains(q);
                        return matchPersona || matchEmpresa || matchMotivo;
                    })
                    .toList();
            model.addAttribute("busqueda", busqueda);
        } else if ((nombrePersona != null && !nombrePersona.isEmpty()) || (empresa != null && !empresa.isEmpty())) {
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
        model.addAttribute("totalAccesos", accesos.size());
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
    public String store(@ModelAttribute("ingreso")@Valid Acceso acceso, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            model.addAttribute("ingreso", acceso);
            model.addAttribute("errors", result.getAllErrors());
            model.addAttribute("tipodocs", tipoDocService.listarTipo_doc());
            model.addAttribute("empresas", empresaService.listarEmpresa());
            model.addAttribute("roles", rolService.obtenerRolPorId(List.of(3L, 4L)));
            return "ingresos/create"; 
        }

        // ========= VALIDACIÓN DE DOC O TELÉFONO YA EXISTENTES ==========
        Optional<Persona> personaDuplicada =
            personaService.buscarPorDocOTelefono(
                    acceso.getPersona().getNumDoc(),
                    acceso.getPersona().getTelefono()
            );

        // ⚠ Si existe Y NO estamos en modo "NuevoIngreso"
        if (personaDuplicada.isPresent()) {

            Persona existente = personaDuplicada.get();

            // Caso: Existe pero NO coincide doc + teléfono → conflicto real
            boolean docIgual = existente.getNumDoc().equals(acceso.getPersona().getNumDoc());
            boolean telIgual = existente.getTelefono().equals(acceso.getPersona().getTelefono());

            String mensajeError = "";

            if (docIgual) mensajeError = "El número de documento ya está registrado.";
            else if (telIgual) mensajeError = "El número de teléfono ya está registrado.";

            model.addAttribute("error", mensajeError);

            // Recargar selects
            model.addAttribute("ingreso", acceso);
            model.addAttribute("tipodocs", tipoDocService.listarTipo_doc());
            model.addAttribute("empresas", empresaService.listarEmpresa());
            model.addAttribute("roles", rolService.obtenerRolPorId(List.of(3L, 4L)));

            return "ingresos/create";
        }
        // ===========================================================

        // Si no existe → crear persona nueva
        Persona persona = acceso.getPersona();
        if (persona.getRol() == null) {
            persona.setRol(rolService.obtenerRol(4L)); // Rol Visitante por defecto
        }

        Persona personaGuardada = personaService.crearPersona(persona);

        // Crear acceso
        acceso.setPersona(personaGuardada);

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
    public String update(@PathVariable Long id,@Valid @ModelAttribute("ingreso") Acceso accesoActualizado,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            model.addAttribute("ingreso", accesoActualizado);
            model.addAttribute("errors", result.getAllErrors());
            model.addAttribute("tipodocs", tipoDocService.listarTipo_doc());
            model.addAttribute("empresas", empresaService.listarEmpresa());
            model.addAttribute("roles", rolService.obtenerRolPorId(List.of(3L, 4L)));
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
                        : LocalDateTime.now());

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

    /*
     * // MOSTRAR DETALLE
     * 
     * @GetMapping("/{id}")
     * public String show(@PathVariable Long id, Model model) {
     * Acceso ingreso = accesoService.obtenerAcceso(id);
     * model.addAttribute("ingreso", ingreso);
     * return "ingresos/show";
     * }
     */
}
