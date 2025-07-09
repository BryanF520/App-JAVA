package com.example.gatekeeper.service;

import com.example.gatekeeper.entities.Tarjeta;

import java.util.List;

public interface TarjetaService {

    List<Tarjeta> listarTarjeta();

    Tarjeta crearTarjeta(Tarjeta tarjeta);

    Tarjeta obtenerTarjeta(Long id);

    Tarjeta actualizarTarjeta(Long id, Tarjeta tarjeta);

    void desactivarTarjeta(Long id);
}
