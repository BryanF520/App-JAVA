package com.example.gatekeeper.service.Impl;

import com.example.gatekeeper.entities.Empresa;
import com.example.gatekeeper.repositories.EmpresaRepository;
import com.example.gatekeeper.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Override
    public List<Empresa> listarEmpresa(){
        return empresaRepository.findAll();
    }

    @Override
    public Empresa crearEmpresa(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    @Override
    public Empresa obtenerEmpresa(Long id) {
        return empresaRepository.findById(id).orElse(null);
    }

    @Override
    public Empresa actualizarEmpresa(Long id, Empresa nuevosDatos) {
        Optional<Empresa> optionalEmpresa = empresaRepository.findById(id);
        if (optionalEmpresa.isPresent()) {
            Empresa empresa = optionalEmpresa.get();

            empresa.setNit(nuevosDatos.getNit());
            empresa.setNombre(nuevosDatos.getNombre());
            empresa.setArea(nuevosDatos.getArea());
            empresa.setContacto(nuevosDatos.getContacto());
            empresa.setUbicacion(nuevosDatos.getUbicacion());

            return empresaRepository.save(empresa);
        }
        return null;
    }

    @Override
    public void desactivarEmpresa(Long id) {
        Optional<Empresa> optionalEmpresa = empresaRepository.findById(id);
        optionalEmpresa.ifPresent(empresaRepository::delete);
    }
}