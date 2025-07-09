package com.example.gatekeeper.service;
import com.example.gatekeeper.entities.Persona;
import java.util.List;
import java.util.Optional;

public interface PersonaService {

    List<Persona> listarPersonas();

    Persona crearPersona(Persona persona);

    Persona obtenerPersona(Long id);

    Optional<Persona> buscarPorDocOTelefono(String doc, String telefono);

    Persona actualizarPersona(Long id, Persona persona);

    void desactivarPersona(Long id);

}
