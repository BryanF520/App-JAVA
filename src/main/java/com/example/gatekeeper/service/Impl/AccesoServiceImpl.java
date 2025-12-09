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
        return accesoRepository.findByEstadoTrue();
    }

    @Override
    public List<Acceso> listarIncactivos(){
        return accesoRepository.findByEstadoFalse();
    }

    @Override
    public Acceso crearAcceso(Acceso acceso){
        return accesoRepository.save(acceso);
    }

    @Override
    public Acceso obtenerAcceso(Long id){
        return accesoRepository.findById(id).orElse(null);
    }

    // Crea consultas automaticas
    @Override
    public List<Acceso> buscar(String nombre, String apellido, LocalDate fecha, Long empresa){
        Specification<Acceso> spec = Specification.where(
            (root, query, cb) -> {

                // Solo accesos activos
                return cb.isTrue(root.get("estado"));
            }
        );

        // FILTRO POR NOMBRE (primer y segundo nombre)
        if (nombre != null && !nombre.isBlank()) {
            spec = spec.and((root, query, cb) ->
                cb.or(
                        cb.like(cb.lower(root.join("persona").get("nombreUno")),
                                "%" + nombre.toLowerCase() + "%"),
                        cb.like(cb.lower(root.join("persona").get("nombreDos")),
                                "%" + nombre.toLowerCase() + "%")
                )
            );
        }

        // FILTRO POR APELLIDO (primer y segundo apellido)
        if (apellido != null && !apellido.isBlank()) {
            spec = spec.and((root, query, cb) ->
                cb.or(
                        cb.like(cb.lower(root.join("persona").get("apellidoUno")),
                                "%" + apellido.toLowerCase() + "%"),
                        cb.like(cb.lower(root.join("persona").get("apellidoDos")),
                                "%" + apellido.toLowerCase() + "%")
                )
            );
        }

        // FILTRO POR FECHA
        if (fecha != null) {
            LocalDateTime inicio = fecha.atStartOfDay();
            LocalDateTime fin = fecha.atTime(LocalTime.MAX);

            spec = spec.and((root, q, cb) ->
                cb.between(root.get("fecha_ingreso"), inicio, fin)
            );
        }

        // FILTRO POR EMPRESA
        if (empresa != null) {
            spec = spec.and((root, query, cb) ->
                cb.equal(root.join("empresa").get("id"), empresa)
            );
        }

        // Persona y Empresa deben estar activas
        spec = spec.and((root, query, cb) ->
            cb.isTrue(root.join("persona").get("estado"))
        );

        spec = spec.and((root, query, cb) ->
            cb.isTrue(root.join("empresa").get("estado"))
        );

        // EJECUTAR BÚSQUEDA
        return accesoRepository.findAll(spec);

    }

    @Override
    public Acceso actualizarAcceso(Long id, Acceso nuevosDatos){
        return accesoRepository.findById(id).map(acceso -> {

            // Mantener persona y fecha original
            // acceso.setPersona(acceso.getPersona());
            // acceso.setFecha_ingreso(acceso.getFecha_ingreso());

            // Editables
            acceso.setMotivo(nuevosDatos.getMotivo());

            if (nuevosDatos.getEmpresa() != null && nuevosDatos.getEmpresa().getId() != null) {
                acceso.setEmpresa(nuevosDatos.getEmpresa());
            }

            return accesoRepository.save(acceso);

        }).orElse(null);
    }

    @Override
    public void desactivarAcceso(Long id){
        Acceso a = obtenerAccesoActivo(id);
        a.setEstado(false);
        accesoRepository.save(a);
    }

    @Override
    public void desactivarAccesoDePersona(Long personaId) {
        List<Acceso> accesos = accesoRepository.findByPersonaIdAndEstadoTrue(personaId);

        for (Acceso acceso : accesos) {
            acceso.setEstado(false);
            accesoRepository.save(acceso);
        }
    }

    @Override
    public void activarAcceso(Long id){
        Acceso a = obtenerAccesoInactivo(id);
        a.setEstado(true);
        accesoRepository.save(a);
    }

    @Override
    public List<Acceso> buscarAccesoActivo(){
        return accesoRepository.findByEstadoTrue();
    }

    @Override
    public List<Acceso> buscarAccesoInactivo(){
        return accesoRepository.findByEstadoFalse();
    }

    @Override
    public Acceso obtenerAccesoActivo(Long id){
        return accesoRepository.findByIdAndEstadoTrue(id)
            .orElseThrow(() -> new IllegalStateException("El acceso no existe o esta desactivado."));
    }

    @Override
    public Acceso obtenerAccesoInactivo(Long id){
        return accesoRepository.findByIdAndEstadoFalse(id)
            .orElseThrow(() -> new IllegalStateException("El acceso no existe o esta activo."));
    }
}
