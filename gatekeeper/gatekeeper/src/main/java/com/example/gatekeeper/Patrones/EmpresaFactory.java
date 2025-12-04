package com.example.gatekeeper.Patrones;

import com.example.gatekeeper.entities.Empresa;

public abstract class EmpresaFactory {
    public abstract Empresa crear(String area, String contacto, Long nit, String nombre, String ubicacion);
}
