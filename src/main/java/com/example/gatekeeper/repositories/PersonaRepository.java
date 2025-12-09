package com.example.gatekeeper.repositories;

import com.example.gatekeeper.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface PersonaRepository extends JpaRepository<Persona,Long>{
    Optional<Persona> findByNumDocOrTelefono(String numDoc, String telefono);
    Optional<Persona> findByNumDoc(String numDoc);
    
    //Buscar por estado (True)
    List<Persona> findByEstadoTrue();

    //Buscar por id y estado (True)
    Optional<Persona> findByIdAndEstadoTrue(Long id);

    //Buscar por estado (False)
    List<Persona> findByEstadoFalse();

    //Buscar por id y estado (False)
    Optional<Persona> findByIdAndEstadoFalse(Long id);
}
