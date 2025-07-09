package com.example.gatekeeper.service.Impl;

import com.example.gatekeeper.entities.Rol;
import com.example.gatekeeper.repositories.RolRepository;
import com.example.gatekeeper.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolServiceImpl implements RolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<Rol> listarRol(){
        return rolRepository.findAll();
    }

    @Override
    public Rol crearRol(Rol rol){
        return rolRepository.save(rol);
    }

    @Override
    public Rol obtenerRol(Long id){
        return rolRepository.findById(id).orElse(null);
    }

    @Override
    public Rol obtenerPorRol(String rol) {
        return rolRepository.findByRol(rol).orElse(null);
    }

    @Override
    public List<Rol> obtenerRolPorId(List<Long> ids) {
        return rolRepository.findByIdIn(ids);
    }

    @Override
    public Rol actualizarRol(Long id, Rol nuevosDatos){
        Optional<Rol> optionalRol = rolRepository.findById(id);
        if (optionalRol.isPresent()){
            Rol rol = optionalRol.get();

            rol.setRol(nuevosDatos.getRol());

            return rolRepository.save(rol);
        }
        return null;
    }

    @Override
    public void desactivarRol(Long id){
        Optional<Rol> optionalRol = rolRepository.findById(id);
        optionalRol.ifPresent(rolRepository::delete);
    }
}
