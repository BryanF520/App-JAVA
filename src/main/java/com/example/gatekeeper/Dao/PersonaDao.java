package com.example.gatekeeper.Dao;
import com.example.gatekeeper.entities.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaDao extends JpaRepository<Persona, Long> {

    //Guarda y verifica si el documento ya existe en la BD
    boolean existsByNumDoc(String numDoc);
}
