package com.example.gatekeeper.seeders;

import com.example.gatekeeper.entities.Rol;
import com.example.gatekeeper.repositories.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class RolSeeder implements Seeder {

    @Autowired
    private RolRepository rolRepo;

    //Método que se ejecuta automáticamente al iniciar la aplicación
    @Override
    public void run() {
        if (rolRepo.count() == 0) {
            rolRepo.save(new Rol(null , "Administrador"));
            rolRepo.save(new Rol(null , "Recepcionista"));
            rolRepo.save(new Rol(null , "Empleado"));
            rolRepo.save(new Rol(null , "Visitante"));
        }
    }
}


