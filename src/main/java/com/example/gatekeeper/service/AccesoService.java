package com.example.gatekeeper.service;
import com.example.gatekeeper.entities.Acceso;

import java.time.LocalDate;
import java.util.List;

public interface AccesoService {

    List<Acceso> listarAcceso();

    List<Acceso> listarIncactivos();

    Acceso crearAcceso(Acceso acceso);

    Acceso obtenerAcceso(Long id);

    List<Acceso> buscar(String nombre, String apellido, LocalDate fecha, Long empresa);

    Acceso actualizarAcceso(Long id, Acceso acceso);

    void desactivarAcceso(Long id);

    void desactivarAccesoDePersona(Long personaId);

    void activarAcceso(Long id);

    // Metodos de busqueda

    List<Acceso> buscarAccesoActivo();

    List<Acceso> buscarAccesoInactivo();

    Acceso obtenerAccesoActivo(Long id);

    Acceso obtenerAccesoInactivo(Long id);
}
