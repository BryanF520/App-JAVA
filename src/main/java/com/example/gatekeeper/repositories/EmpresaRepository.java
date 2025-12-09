package com.example.gatekeeper.repositories;

import com.example.gatekeeper.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    // Buscar una empresa por su NIT
    Optional<Empresa> findByNit(Long nit);

    // Buscar una empresa por su nombre
    Optional<Empresa> findByNombre(String nombre);
    // Verificar si ya existe un NIT registrado si es asi manda true
    boolean existsByNit(Long nit);

    // Verificar si ya existe un nombre registrado
    boolean existsByNombre(String nombre);

    // Búsqueda por nombre (case-insensitive)
    List<Empresa> findByNombreContainingIgnoreCase(String nombre);

    // Búsqueda por área (case-insensitive)
    List<Empresa> findByAreaContainingIgnoreCase(String area);

    // Búsqueda por contacto (case-insensitive)
    List<Empresa> findByContactoContainingIgnoreCase(String contacto);

    // Busqueda por estado (True)
    List<Empresa> findByEstadoTrue();

    // Busqueda por id y estado (True)
    Optional<Empresa> findByIdAndEstadoTrue(Long id);

    // Búsqueda por estado (False)
    List<Empresa> findByEstadoFalse();

    // Busqueda por id y estado (False)

    Optional<Empresa> findByIdAndEstadoFalse(Long id);

    // Búsqueda combinada por nombre, área y contacto
    @Query("SELECT e FROM Empresa e WHERE " +
           "LOWER(e.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(e.area) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(e.contacto) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Empresa> buscarPorTermino(@Param("busqueda") String busqueda);
}
