package com.example.gatekeeper.service.Impl;

import com.example.gatekeeper.entities.Persona;
import com.example.gatekeeper.entities.Acceso;
import com.example.gatekeeper.repositories.PersonaRepository;
import com.example.gatekeeper.repositories.AccesoRepository;
import com.example.gatekeeper.service.AccesoService;
import com.example.gatekeeper.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaServiceImpl implements PersonaService {

    
    private final PersonaRepository personaRepository;
    private final AccesoRepository accesoRepository;
    private final AccesoService accesoService;
    private final PasswordEncoder passwordEncoder;

    public PersonaServiceImpl(PersonaRepository personaRepository,
                              PasswordEncoder passwordEncoder, AccesoRepository accesoRepository, AccesoService accesoService) {
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
        this.accesoRepository = accesoRepository;
        this.accesoService = accesoService;
    }


    @Override
    public List<Persona> listarPersonas(){
        return personaRepository.findByEstadoTrue();
    }

    @Override 
    public List<Persona> listarInactivos() {
        return personaRepository.findByEstadoFalse();
    }

    @Override
    public Persona crearPersona(Persona persona){
        return personaRepository.save(persona);
    }

    @Override
    public Persona obtenerPersona(Long id){
        return personaRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<Persona> buscarPorDocOTelefono(String doc, String telefono) {
        return personaRepository.findByNumDocOrTelefono(doc, telefono);
    }

    @Override
    public Persona actualizarPersona(Long id, Persona nuevosDatos){
        
        Persona persona = personaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        // Actualizar campos normales
        persona.setTipoDoc(nuevosDatos.getTipoDoc());
        persona.setNumDoc(nuevosDatos.getNumDoc());
        persona.setNombreUno(nuevosDatos.getNombreUno());
        persona.setNombreDos(nuevosDatos.getNombreDos());
        persona.setApellidoUno(nuevosDatos.getApellidoUno());
        persona.setApellidoDos(nuevosDatos.getApellidoDos());
        persona.setTelefono(nuevosDatos.getTelefono());
        persona.setRol(nuevosDatos.getRol());

        String nuevaContra = nuevosDatos.getPassword();
        // Si viene vacía o null → NO tocar la contraseña
        if (nuevaContra == null || nuevaContra.isBlank()) {
            return personaRepository.save(persona);
        }

        // Si enviaron una nueva contraseña:
        String contraActual = persona.getPassword();
        if (contraActual != null && contraActual.equals(nuevaContra)) {
            // No hay cambio real
            return personaRepository.save(persona);
        }
    
        if (contraActual == null || !passwordEncoder.matches(nuevaContra, contraActual)) {
            persona.setPassword(passwordEncoder.encode(nuevaContra));
        }
                        
        return personaRepository.save(persona);
        
    }

    @Override
    public void desactivarPersona(Long id){
        Persona p = obtenerPersonaActiva(id);

        accesoService.desactivarAccesoDePersona(id);

        p.setEstado(false);
        personaRepository.save(p);
    }


    @Override
    public void activarPersona(Long id){
        Persona p = obtenerPersonaInactiva(id);
        p.setEstado(true);
        personaRepository.save(p);
    }

    @Override
    public Persona obtenerPersonaActiva(Long id) {
        return personaRepository.findByIdAndEstadoTrue(id)
                .orElseThrow(() -> new RuntimeException("La persona no existe o está inactiva"));
    }

    @Override
    public Persona obtenerPersonaInactiva(Long id) {
        return personaRepository.findByIdAndEstadoFalse(id)
                .orElseThrow(() -> new RuntimeException("La persona no existe o está activa"));
    }

    @Override
    public List<Persona> buscarPersonasActivas() {
        return personaRepository.findByEstadoTrue();
    }

    @Override
    public List<Persona> buscarPersonasInactivas() {
        return personaRepository.findByEstadoFalse();
    }
}
