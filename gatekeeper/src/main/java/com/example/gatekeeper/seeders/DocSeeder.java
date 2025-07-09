package com.example.gatekeeper.seeders;

import com.example.gatekeeper.entities.Tipo_doc;
import com.example.gatekeeper.repositories.Tipo_docRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DocSeeder {
    @Bean
    CommandLineRunner seedTipoDocs(Tipo_docRepository tipoDocRepository) {
        return args -> {
            if(tipoDocRepository.count() == 0) {
                tipoDocRepository.saveAll(List.of(
                    new Tipo_doc(null, "Cédula de Ciudadanía"),
                    new Tipo_doc(null, "Tarjeta de Identidad"),
                    new Tipo_doc(null, "Cédula de Extranjería"),
                    new Tipo_doc(null, "Pasaporte")
                ));
            }
        };
    }
}
