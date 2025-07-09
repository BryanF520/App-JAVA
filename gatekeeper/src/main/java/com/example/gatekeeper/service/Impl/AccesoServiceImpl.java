package com.example.gatekeeper.service.Impl;
import org.springframework.data.jpa.domain.Specification;
import com.example.gatekeeper.entities.Acceso;
import com.example.gatekeeper.repositories.AccesoRepository;
import com.example.gatekeeper.repositories.EmpresaRepository;
import com.example.gatekeeper.repositories.PersonaRepository;
import com.example.gatekeeper.service.AccesoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class AccesoServiceImpl implements AccesoService {

    @Autowired
    private AccesoRepository accesoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Override
    public List<Acceso> listarAcceso(){
        return accesoRepository.findAll();
    }

    @Override
    public Acceso crearAcceso(Acceso acceso){
        return accesoRepository.save(acceso);
    }

    @Override
    public Acceso obtenerAcceso(Long id){
        return accesoRepository.findById(id).orElse(null);
    }

//    Crea consultas automaticas
    @Override
    public List<Acceso> buscar(String nombre, String apellido, LocalDate fecha, String empresa){

        Specification<Acceso> spec = Specification.where(null);

        if (nombre != null && !nombre.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.join("persona").get("nombreUno")),
                                    "%" + nombre.toLowerCase() + "%"),
                            cb.like(cb.lower(root.join("persona").get("nombreDos")),
                                    "%" + nombre.toLowerCase() + "%")
                    ));
        }

        if (apellido != null && !apellido.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.join("persona").get("apellidoUno")),
                                    "%" + apellido.toLowerCase() + "%"),
                            cb.like(cb.lower(root.join("persona").get("apellidoDos")),
                                    "%" + apellido.toLowerCase() + "%")
                    ));
        }

        if (fecha != null) {
            LocalDateTime inicio = fecha.atStartOfDay();
            LocalDateTime fin = fecha.atTime(LocalTime.MAX);

            spec = spec.and((root, q, cb) ->
                    cb.between(root.get("fecha_ingreso"), inicio, fin));
        }

        if (empresa != null && !empresa.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.join("empresa").get("nombre")),
                            "%" + empresa.toLowerCase() + "%"));
        }

        return accesoRepository.findAll(spec);
    }

    @Override
    public Acceso actualizarAcceso(Long id, Acceso nuevosDatos){
        Optional<Acceso> optionalAcceso = accesoRepository.findById(id);
        if (optionalAcceso.isPresent()) {
            Acceso acceso = optionalAcceso.get();

            acceso.setPersona(nuevosDatos.getPersona());
            acceso.setEmpresa(nuevosDatos.getEmpresa());
//            acceso.setFechaIngreso(nuevosDatos.getFechaIngreso()) PENDIENTE;

            return accesoRepository.save(acceso);
        }

        return null;
    }

    @Override
    public void desactivarAcceso(Long id){
        Optional<Acceso> optionalAcceso = accesoRepository.findById(id);
        optionalAcceso.ifPresent(accesoRepository::delete);
    }
}
