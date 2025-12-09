package com.example.gatekeeper.seeders;

import com.example.gatekeeper.entities.Persona;
import com.example.gatekeeper.entities.Rol;
import com.example.gatekeeper.repositories.PersonaRepository;
import com.example.gatekeeper.repositories.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PersonaSeeder implements Seeder {

    @Autowired
    private PersonaRepository personaRepo;

    @Autowired
    private RolRepository rolRepo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public void run() {
        if (personaRepo.findByNumDoc("123456789").isEmpty()) {
            Rol adminRol = rolRepo.findByRol("Administrador")
                    .orElseThrow(() -> new RuntimeException("Rol 'Administrador' no existe"));

            Persona admin = new Persona();
            admin.setNumDoc("123456789");
            admin.setNombreUno("Admin");
            admin.setApellidoUno("Principal");
            admin.setTelefono("3123456789");
            admin.setRol(adminRol);
            admin.setPassword(encoder.encode("admin123"));

            personaRepo.save(admin);
        }
    }
}

