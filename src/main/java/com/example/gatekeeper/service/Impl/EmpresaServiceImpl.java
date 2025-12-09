package com.example.gatekeeper.service.Impl;

import com.example.gatekeeper.entities.Empresa;
import com.example.gatekeeper.repositories.EmpresaRepository;
import com.example.gatekeeper.repositories.AccesoRepository; //  cambio: import para validar accesos
import com.example.gatekeeper.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaServiceImpl implements EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private AccesoRepository accesoRepository; // cambio: inyección de AccesoRepository

    @Override
    public List<Empresa> listarEmpresa(){
        return empresaRepository.findByEstadoTrue();
    }

    @Override
    public List<Empresa> listarInactivos() {
        return empresaRepository.findByEstadoFalse();
    }

    @Override
    public Empresa crearEmpresa(Empresa empresa) {
        if (empresaRepository.existsByNit(empresa.getNit())) {
            throw new IllegalArgumentException("El NIT " + empresa.getNit() + " ya está registrado.");
        }
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

            //Esto es por si se intenta cambiar el nit de la empresa se valida por si ya existe
            if (!empresa.getNit().equals(nuevosDatos.getNit())){
                Optional<Empresa>duplicado = empresaRepository.findByNit(nuevosDatos.getNit());
                if (duplicado.isPresent() && !duplicado.get().getId().equals(id)){
                    throw new IllegalArgumentException("El nit" + nuevosDatos.getNit() + "ya está en uso.");
                }
            }

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
        //  validación antes de eliminar
        Long accesosActivos = accesoRepository.countByEmpresaIdAndEstadoTrue(id);
        if (accesosActivos != null && accesosActivos > 0) {
            throw new IllegalStateException("No se puede eliminar la empresa porque tiene accesos asociados.");
        }

        Empresa e = obtenerEmpresaActiva(id);
        e.setEstado(false);
        empresaRepository.save(e);
    }

    @Override
    public void activarEmpresa(Long id) {
        Empresa e = obtenerEmpresaInactiva(id);
        e.setEstado(true);
        empresaRepository.save(e);
    }
    // verifica en BD si ya existe un nit
    @Override
    public boolean existeNit(Long nit) {
        return empresaRepository.existsByNit(nit);
    }

    // Verifica en BD si ya existe un nit al editar
    @Override
    public boolean existeNitEditar(Long id, Long nit) {
        Optional<Empresa> empresa = empresaRepository.findByNit(nit);
        return empresa.isPresent() && !empresa.get().getId().equals(id);
    }
    // Verifica en BD si ya existe un nombre
    @Override
    public boolean existeNombre(String nombre) {
        return empresaRepository.existsByNombre(nombre);
    }

    // Verifica en BD si ya existe un nombre al editar
    @Override
    public boolean existeNombreEditar(Long id, String nombre) {
        Optional<Empresa> empresa = empresaRepository.findByNombre(nombre);
        return empresa.isPresent() && !empresa.get().getId().equals(id);
    }

    @Override
    public List<Empresa> buscarPorNombre(String nombre) {
        return empresaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<Empresa> buscarPorArea(String area) {
        return empresaRepository.findByAreaContainingIgnoreCase(area);
    }

    @Override
    public List<Empresa> buscarPorContacto(String contacto) {
        return empresaRepository.findByContactoContainingIgnoreCase(contacto);
    }

    @Override
    public List<Empresa> buscarPorTermino(String busqueda) {
        return empresaRepository.buscarPorTermino(busqueda);
    }

    @Override
    public List<Empresa> buscarEmpresasActivas() {
        return empresaRepository.findByEstadoTrue();
    }

    @Override
    public Empresa obtenerEmpresaActiva(Long id) {
        return empresaRepository.findByIdAndEstadoTrue(id)
            .orElseThrow(() -> new IllegalStateException("La empresa no existe o esta inactiva"));
    }

    @Override
    public Empresa obtenerEmpresaInactiva(Long id) {
        return empresaRepository.findByIdAndEstadoFalse(id)
            .orElseThrow(() -> new IllegalStateException("La empresa no existe o ya esta activa"));
    }
    @Override
    public List<Empresa> buscarEmpresasInactivas() {
        return empresaRepository.findByEstadoFalse();
    }
}
