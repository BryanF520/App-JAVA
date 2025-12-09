package com.example.gatekeeper.service;

import com.example.gatekeeper.entities.Empresa;

import java.util.List;

public interface EmpresaService {

    List<Empresa> listarEmpresa();

    List<Empresa> listarInactivos();

    Empresa crearEmpresa(Empresa empresa);

    Empresa obtenerEmpresa(Long id);

    Empresa actualizarEmpresa(Long id, Empresa empresa);

    void desactivarEmpresa(Long id);

    void activarEmpresa(Long id);

    boolean existeNit(Long nit);

    boolean existeNitEditar(Long id, Long nit);
    
    boolean existeNombre(String nombre);

    boolean existeNombreEditar(Long id, String nombre);

    // Métodos de búsqueda
    List<Empresa> buscarPorNombre(String nombre);

    List<Empresa> buscarPorArea(String area);

    List<Empresa> buscarPorContacto(String contacto);

    List<Empresa> buscarPorTermino(String busqueda);

    List<Empresa> buscarEmpresasActivas();

    Empresa obtenerEmpresaActiva(Long id);
    
    Empresa obtenerEmpresaInactiva(Long id);
    
    List<Empresa> buscarEmpresasInactivas();
}
