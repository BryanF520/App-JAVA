package com.example.gatekeeper.Dao;
import com.example.gatekeeper.entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaDao extends JpaRepository<Empresa, Long> {

    //Guarda y verifica si el nit ya existe en la BD
    boolean existsByNit(Long nit);
    //Guarda y verifica si el nombre ya existe en la BD
    boolean existsByNombre(String nombre);
}
