package com.example.gatekeeper.repositories;

import com.example.gatekeeper.entities.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccesoRepository extends JpaRepository<Acceso, Long>, JpaSpecificationExecutor<Acceso>{

}
