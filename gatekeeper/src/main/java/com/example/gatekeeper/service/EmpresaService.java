package com.example.gatekeeper.service;

import com.example.gatekeeper.entities.Empresa;

import java.util.List;

public interface EmpresaService {

    List<Empresa> listarEmpresa();

    Empresa crearEmpresa(Empresa empresa);

    Empresa obtenerEmpresa(Long id);

    Empresa actualizarEmpresa(Long id, Empresa empresa);

    void desactivarEmpresa(Long id);

}
