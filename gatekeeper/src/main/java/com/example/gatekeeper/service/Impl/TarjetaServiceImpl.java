package com.example.gatekeeper.service.Impl;

import com.example.gatekeeper.entities.Tarjeta;
import com.example.gatekeeper.repositories.PersonaRepository;
import com.example.gatekeeper.repositories.TarjetaRepository;
import com.example.gatekeeper.service.TarjetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TarjetaServiceImpl implements TarjetaService {

    @Autowired
    private TarjetaRepository tarjetaRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Override
    public List<Tarjeta> listarTarjeta(){
        return tarjetaRepository.findAll();
    }

    @Override
    public Tarjeta crearTarjeta(Tarjeta tarjeta){
        return tarjetaRepository.save(tarjeta);
    }

    @Override
    public Tarjeta obtenerTarjeta(Long id){
        return tarjetaRepository.findById(id).orElse(null);
    }

    @Override
    public Tarjeta actualizarTarjeta(Long id, Tarjeta nuevosDatos){
        Optional<Tarjeta> optionalTarjeta = tarjetaRepository.findById(id);
        if (optionalTarjeta.isPresent()){
            Tarjeta tarjeta = optionalTarjeta.get();

            tarjeta.setPersona_id(nuevosDatos.getPersona_id());
            tarjeta.setEstado(nuevosDatos.getEstado());

            return tarjetaRepository.save(tarjeta);
        }

        return null;
    }

    @Override
    public void desactivarTarjeta(Long id){
        Optional<Tarjeta> optionalTarjeta = tarjetaRepository.findById(id);
        optionalTarjeta.ifPresent(tarjetaRepository::delete);
    }
}
