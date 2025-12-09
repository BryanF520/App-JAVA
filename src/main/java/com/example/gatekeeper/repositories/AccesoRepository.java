package com.example.gatekeeper.repositories;

import com.example.gatekeeper.entities.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccesoRepository extends JpaRepository<Acceso, Long>, JpaSpecificationExecutor<Acceso>{
    // Totales por día (solo accesos activos y vinculados a persona/empresa activa)
    @Query(value = """
        SELECT DATE(a.fecha_ingreso) AS fecha, COUNT(*) AS total 
        FROM accesos a
        JOIN personas p ON a.persona_id = p.id
        JOIN empresas e ON a.empresa_id = e.id
        WHERE a.estado = true AND p.estado = true AND e.estado = true
        GROUP BY DATE(a.fecha_ingreso)
        ORDER BY fecha DESC
    """, nativeQuery = true)
    List<Object[]> countByDate();


    // Totales por mes
    @Query(value = """
        SELECT YEAR(a.fecha_ingreso) AS year, MONTH(a.fecha_ingreso) AS month, COUNT(*) AS total 
        FROM accesos a
        JOIN personas p ON a.persona_id = p.id
        JOIN empresas e ON a.empresa_id = e.id
        WHERE a.estado = true AND p.estado = true AND e.estado = true
        GROUP BY YEAR(a.fecha_ingreso), MONTH(a.fecha_ingreso)
        ORDER BY year DESC, month DESC
    """, nativeQuery = true)
    List<Object[]> countByMonth();


    // Totales por año
    @Query(value = """
        SELECT YEAR(a.fecha_ingreso) AS year, COUNT(*) AS total 
        FROM accesos a
        JOIN personas p ON a.persona_id = p.id
        JOIN empresas e ON a.empresa_id = e.id
        WHERE a.estado = true AND p.estado = true AND e.estado = true
        GROUP BY YEAR(a.fecha_ingreso)
        ORDER BY year DESC
    """, nativeQuery = true)
    List<Object[]> countByYear();


    // Totales por empresa
    @Query(value = """
        SELECT e.id AS empresa_id, e.nombre AS nombre, COUNT(*) AS total
        FROM accesos a
        JOIN empresas e ON a.empresa_id = e.id
        JOIN personas p ON a.persona_id = p.id
        WHERE a.estado = true AND p.estado = true AND e.estado = true
        GROUP BY e.id, e.nombre
        ORDER BY total DESC
    """, nativeQuery = true)
    List<Object[]> countByCompany();


    // Totales por empresa por mes
    @Query(value = """
        SELECT e.id AS empresa_id, e.nombre AS nombre, 
               YEAR(a.fecha_ingreso) AS year, MONTH(a.fecha_ingreso) AS month, COUNT(*) AS total
        FROM accesos a
        JOIN empresas e ON a.empresa_id = e.id
        JOIN personas p ON a.persona_id = p.id
        WHERE a.estado = true AND p.estado = true AND e.estado = true
        GROUP BY e.id, year, month
        ORDER BY e.nombre, year DESC, month DESC
    """, nativeQuery = true)
    List<Object[]> countByCompanyMonth();


    // Totales por empresa por año
    @Query(value = """
        SELECT e.id AS empresa_id, e.nombre AS nombre, 
               YEAR(a.fecha_ingreso) AS year, COUNT(*) AS total
        FROM accesos a
        JOIN empresas e ON a.empresa_id = e.id
        JOIN personas p ON a.persona_id = p.id
        WHERE a.estado = true AND p.estado = true AND e.estado = true
        GROUP BY e.id, year
        ORDER BY e.nombre, year DESC
    """, nativeQuery = true)
    List<Object[]> countByCompanyYear();

    // contar accesos asociados a una empresa
    long countByEmpresaId(Long empresaId);

    long countByEmpresaIdAndEstadoTrue(Long empresaId);

    // Busqueda por id persona y estado (True)

    List<Acceso> findByPersonaIdAndEstadoTrue(Long personaId);

    // Busqueda por estado (True)
    List<Acceso> findByEstadoTrue();

    //Busqueda por id y estado (True)
    Optional<Acceso> findByIdAndEstadoTrue(Long id);

    // Busqueda por estado (False)
    List<Acceso> findByEstadoFalse();

    //Busqueda por id y estado (False)
    Optional<Acceso> findByIdAndEstadoFalse(Long id);

    @Query("SELECT COUNT(a) FROM Acceso a WHERE DATE(a.fecha_ingreso) = :dia")
    Long countByFechaIngresoDia(LocalDate dia);

    @Query("SELECT COUNT(a) FROM Acceso a WHERE FUNCTION('DATE_FORMAT', a.fecha_ingreso, '%Y-%m') = :mes")
    Long countByFechaIngresoMes(String mes);

    @Query("SELECT COUNT(a) FROM Acceso a WHERE YEAR(a.fecha_ingreso) = :anio")
    Long countByFechaIngresoAnio(Integer anio);

    @Query("SELECT COUNT(a) FROM Acceso a WHERE a.empresa.id = :empresaId")
    Long countByEmpresa(Long empresaId);
}

