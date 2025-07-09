package com.example.gatekeeper.service;

import com.example.gatekeeper.entities.Tipo_doc;

import java.util.List;

public interface Tipo_docService {

    List<Tipo_doc> listarTipo_doc();

    Tipo_doc crearTipo_doc(Tipo_doc tipo_doc);

    Tipo_doc obtenerTipo_doc(Long id);

    Tipo_doc actualizarTipo_doc(Long id, Tipo_doc tipo_doc);

    void desactivarTipo_doc(Long id);

}
