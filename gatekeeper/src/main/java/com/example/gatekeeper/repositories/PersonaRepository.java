package com.example.gatekeeper.repositories;

import com.example.gatekeeper.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona,Long>{
    Optional<Persona> findByNumDocOrTelefono(String numDoc, String telefono);
    Optional<Persona> findByNumDoc(String numDoc);
    
}
