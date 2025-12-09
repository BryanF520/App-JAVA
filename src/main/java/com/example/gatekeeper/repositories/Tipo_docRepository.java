package com.example.gatekeeper.repositories;

import com.example.gatekeeper.entities.Tipo_doc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Tipo_docRepository extends JpaRepository<Tipo_doc, Long> {

}
