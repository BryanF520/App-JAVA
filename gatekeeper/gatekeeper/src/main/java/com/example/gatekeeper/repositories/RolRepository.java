package com.example.gatekeeper.repositories;

import com.example.gatekeeper.entities.Rol;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends  JpaRepository<Rol, Long>{
    List<Rol> findByIdIn(List<Long> ids);
    Optional<Rol> findByRol(String rol);
}
