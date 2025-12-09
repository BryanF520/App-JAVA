package com.example.gatekeeper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.gatekeeper.entities.Acceso;
import com.example.gatekeeper.entities.Empresa;
import com.example.gatekeeper.entities.Persona;
import com.example.gatekeeper.entities.Rol;
import com.example.gatekeeper.entities.Tipo_doc;
import com.example.gatekeeper.service.AccesoService;
import com.example.gatekeeper.service.EmpresaService;
import com.example.gatekeeper.service.PersonaService;
import com.example.gatekeeper.service.RolService;
import com.example.gatekeeper.service.Tipo_docService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Collection;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private AccesoService accesoService;

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
    public String index(Model model, 
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String numDoc,
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String busqueda){
        List<Persona> personas;
        // If a unified 'busqueda' param is provided, prefer it and search across multiple fields
        if (busqueda != null && !busqueda.isEmpty()) {
            String q = busqueda.toLowerCase();
            personas = personaService.listarPersonas().stream()
                    .filter(p -> {
                        return (p.getNombreUno() != null && p.getNombreUno().toLowerCase().contains(q)) ||
                               (p.getNombreDos() != null && p.getNombreDos().toLowerCase().contains(q)) ||
                               (p.getApellidoUno() != null && p.getApellidoUno().toLowerCase().contains(q)) ||
                               (p.getApellidoDos() != null && p.getApellidoDos().toLowerCase().contains(q)) ||
                               (p.getNumDoc() != null && p.getNumDoc().toLowerCase().contains(q)) ||
                               (p.getTelefono() != null && p.getTelefono().toLowerCase().contains(q)) ||
                               (p.getRol() != null && p.getRol().getRol() != null && p.getRol().getRol().toLowerCase().contains(q));
                    })
                    .toList();
            model.addAttribute("busqueda", busqueda);
        } else if ((nombre != null && !nombre.isEmpty()) || (numDoc != null && !numDoc.isEmpty()) || (rol != null && !rol.isEmpty())) {
            personas = personaService.listarPersonas().stream()
                    .filter(p -> {
                        boolean coincideNombre = nombre == null || nombre.isEmpty() || 
                            (p.getNombreUno() != null && p.getNombreUno().toLowerCase().contains(nombre.toLowerCase())) ||
                            (p.getApellidoUno() != null && p.getApellidoUno().toLowerCase().contains(nombre.toLowerCase()));
                        boolean coincideDoc = numDoc == null || numDoc.isEmpty() || 
                            (p.getNumDoc() != null && p.getNumDoc().contains(numDoc));
                        boolean coincideRol = rol == null || rol.isEmpty() || 
                            (p.getRol() != null && p.getRol().getRol() != null && 
                             p.getRol().getRol().toLowerCase().contains(rol.toLowerCase()));
                        return coincideNombre && coincideDoc && coincideRol;
                    })
                    .toList();
            model.addAttribute("filtroNombre", nombre);
            model.addAttribute("filtroNumDoc", numDoc);
            model.addAttribute("filtroRol", rol);
        } else {
            personas = personaService.listarPersonas();
        }
        model.addAttribute("personas", personas);
        model.addAttribute("totalPersonas", personas.size());
        return "ingresos/personas/index";
    }

    @GetMapping("/inactivos")
    public String listarInactivos(Model model) {
        model.addAttribute("personas", personaService.listarInactivos());
        return "ingresos/personas/inactivos";
    }
    
    @GetMapping("/create")
    public String create(Model model, Authentication auth){

         List<Rol> roles = rolService.obtenerRolPorId(List.of(1L, 2L)); // Solo Admin y Recepcionista
        List<Tipo_doc> tipo_doc = tipoDocService.listarTipo_doc();
        model.addAttribute("roles", roles);
        model.addAttribute("tipo_doc", tipo_doc);
        model.addAttribute("persona", new Persona());
        return "ingresos/personas/create";
    }

    @PostMapping
    public String store(@ModelAttribute @Valid Persona persona, BindingResult result, Authentication auth, Model model) {
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

        if (persona.getId() == null) { // solo al crear
            if (persona.getPassword() == null || persona.getPassword().length() < 6) {
            result.rejectValue("password", "error.password", "La contraseña debe tener mínimo 6 caracteres.");
            }
        }

        // Encriptar la contraseña si fue ingresada
        if (persona.getPassword() != null && !persona.getPassword().isBlank()) {
        persona.setPassword(passwordEncoder.encode(persona.getPassword()));
        }

        if(result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            model.addAttribute("roles", rolService.obtenerRolPorId(List.of(1L, 2L)));
            model.addAttribute("tipo_doc", tipoDocService.listarTipo_doc());
            return "ingresos/personas/create";
        }

        try {
            personaService.crearPersona(persona);
            return "redirect:/ingresos/personas";
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            model.addAttribute("error", "Ya existe un usuario con ese número de documento");
            // Reagregar los combos al modelo para que la vista 'create' no falle
            model.addAttribute("roles", rolService.obtenerRolPorId(List.of(1L, 2L)));
            model.addAttribute("tipo_doc", tipoDocService.listarTipo_doc());
            return "ingresos/personas/create";
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error inesperado al crear la persona.");
            model.addAttribute("roles", rolService.obtenerRolPorId(List.of(1L, 2L)));
            model.addAttribute("tipo_doc", tipoDocService.listarTipo_doc());
            return "ingresos/personas/create";
        }
    }

    //Seccion para dar nuevo ingreso a una persona ya creada
    @GetMapping("/nuevoIngreso/{id}")
    public String nuevoIngreso(@PathVariable Long id, Model model) {
        Persona persona = personaService.obtenerPersona(id);

        if (persona == null) {
            return "redirect:/personas?error=PersonaNoEncontrada";
        }

        Acceso nuevoAcceso = new Acceso();
        nuevoAcceso.setPersona(persona);
        nuevoAcceso.setFecha_ingreso(LocalDateTime.now());

        model.addAttribute("acceso", nuevoAcceso);
        model.addAttribute("empresas", empresaService.listarEmpresa());

        return "ingresos/personas/nuevoIngreso"; 
    }

    // Guardar el nuevo ingreso de la persona
    @PostMapping("/guardarIngreso")
    public String guardarIngreso(
        @RequestParam("empresa.id") Long empresaId,
        @RequestParam("motivo") String motivo,
        @RequestParam("persona.id") Long personaId
        ) {

        Persona persona = personaService.obtenerPersona(personaId);
        Empresa empresa = empresaService.obtenerEmpresa(empresaId);

        Acceso nuevoAcceso = new Acceso();
        nuevoAcceso.setPersona(persona);
        nuevoAcceso.setEmpresa(empresa);
        nuevoAcceso.setMotivo(motivo);
        nuevoAcceso.setFecha_ingreso(LocalDateTime.now());

        accesoService.crearAcceso(nuevoAcceso);

        return "redirect:/ingresos";
    }

    @GetMapping("/show/{id}")
    public String show(@PathVariable Long id, Model model) {
        Persona persona = personaService.obtenerPersona(id);
        model.addAttribute("persona", persona);
        return "ingresos/personas/show";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Persona persona = personaService.obtenerPersona(id);
        List<Rol> roles = rolService.listarRol();
        List<Tipo_doc> tipodocs = tipoDocService.listarTipo_doc();

        if (persona != null) {
            persona.setPassword(null);
        }

        model.addAttribute("persona", persona);
        model.addAttribute("roles", roles);
        model.addAttribute("tipodocs", tipodocs);
        return "ingresos/personas/edit";
    }

   @PostMapping("/{id}/update")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("persona") Persona personaForm,
            BindingResult result,
            Model model
        ) {

        Persona actual = personaService.obtenerPersona(id);

        if (actual == null) {
            model.addAttribute("error", "La persona no existe.");
            return "ingresos/personas/edit";
        }     

        // VALIDAR SOLO SI SE INGRESÓ UNA CONTRASEÑA NUEVA
        if (personaForm.getPassword() != null && !personaForm.getPassword().isBlank()) {
            if (personaForm.getPassword().length() < 6) {
                result.rejectValue("password", "error.password", "La contraseña debe tener al menos 6 caracteres.");
            }   
        }

        // SI HAY ERRORES → RETORNAR FORMULARIO
        if (result.hasErrors()) {
            model.addAttribute("errors", result.getAllErrors());
            model.addAttribute("roles", rolService.listarRol());
            model.addAttribute("tipodocs", tipoDocService.listarTipo_doc());
            model.addAttribute("persona", personaForm);
            return "ingresos/personas/edit";
        }

        // ACTUALIZAR CAMPOS (MENOS CONTRASEÑA)
        actual.setTipoDoc(personaForm.getTipoDoc());
        actual.setNumDoc(personaForm.getNumDoc());
        actual.setNombreUno(personaForm.getNombreUno());
        actual.setNombreDos(personaForm.getNombreDos());
        actual.setApellidoUno(personaForm.getApellidoUno());
        actual.setApellidoDos(personaForm.getApellidoDos());
        actual.setTelefono(personaForm.getTelefono());
        actual.setRol(personaForm.getRol());


        personaService.actualizarPersona(id, personaForm);

        return "redirect:/ingresos/personas";
    }



    @PostMapping("/{id}/desactivar")
    public String destroy(@PathVariable Long id, Model model) {

        try {
            personaService.desactivarPersona(id);
            return "redirect:/ingresos/personas";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            List<Persona> personas = personaService.listarPersonas();
            model.addAttribute("personas", personas);
            return "ingresos/personas/index";
        }
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable Long id, RedirectAttributes redir) {
         try {
            personaService.activarPersona(id);
            redir.addFlashAttribute("success", "Persona activada correctamente.");
            return "redirect:/ingresos/personas";
        } catch (Exception e) {
            redir.addFlashAttribute("error", e.getMessage());
            return "redirect:/ingresos/personas/inactivos";
        }
    }
}