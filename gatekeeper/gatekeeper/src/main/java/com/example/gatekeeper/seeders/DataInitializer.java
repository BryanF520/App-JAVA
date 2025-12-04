package com.example.gatekeeper.seeders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private RolSeeder rolSeeder;

    @Autowired
    private PersonaSeeder personaSeeder;

    
    @Override
    public void run(String... args) throws Exception {
        rolSeeder.run();
        personaSeeder.run();
    }
}
