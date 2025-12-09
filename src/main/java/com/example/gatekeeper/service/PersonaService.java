package com.example.gatekeeper.service;
import com.example.gatekeeper.entities.Persona;
import java.util.List;
import java.util.Optional;

public interface PersonaService {

    List<Persona> listarPersonas();

    List<Persona> listarInactivos();

    Persona crearPersona(Persona persona);

    Persona obtenerPersona(Long id);

    Optional<Persona> buscarPorDocOTelefono(String doc, String telefono);

    Persona actualizarPersona(Long id, Persona persona);

    void desactivarPersona(Long id);

    void activarPersona(Long id);

    // BUSQUEDAS

    Persona obtenerPersonaActiva(Long id);

    Persona obtenerPersonaInactiva(Long id);

    List<Persona> buscarPersonasActivas();

    List<Persona> buscarPersonasInactivas();

}
