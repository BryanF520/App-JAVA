package com.example.gatekeeper.service;
import com.example.gatekeeper.entities.Acceso;

import java.time.LocalDate;
import java.util.List;

public interface AccesoService {

    List<Acceso> listarAcceso();

    Acceso crearAcceso(Acceso acceso);

    Acceso obtenerAcceso(Long id);

    List<Acceso> buscar(String nombre, String apellido, LocalDate fecha, String empresa);

    Acceso actualizarAcceso(Long id, Acceso acceso);

    void desactivarAcceso(Long id);


}
