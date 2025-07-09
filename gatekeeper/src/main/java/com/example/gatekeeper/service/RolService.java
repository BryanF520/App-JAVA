package com.example.gatekeeper.service;
import com.example.gatekeeper.entities.Rol;


import java.util.List;

public interface RolService {

    List<Rol> listarRol();

    Rol crearRol(Rol rol);

    Rol obtenerRol(Long id);

    Rol obtenerPorRol(String rol);

    List<Rol> obtenerRolPorId(List<Long> ids);
    
    Rol actualizarRol(Long id, Rol rol);

    void desactivarRol(Long id);
}
