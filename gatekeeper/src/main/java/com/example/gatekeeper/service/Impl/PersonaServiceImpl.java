package com.example.gatekeeper.service.Impl;

import com.example.gatekeeper.entities.Persona;
import com.example.gatekeeper.repositories.PersonaRepository;
import com.example.gatekeeper.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonaServiceImpl implements PersonaService {

    @Autowired
    private PersonaRepository personaRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<Persona> listarPersonas(){
        return personaRepository.findAll();
    }

    @Override
    public Persona crearPersona(Persona persona){
        return personaRepository.save(persona);
    }

    @Override
    public Persona obtenerPersona(Long id){
        return personaRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<Persona> buscarPorDocOTelefono(String doc, String telefono) {
        return personaRepository.findByNumDocOrTelefono(doc, telefono);
    }

    @Override
    public Persona actualizarPersona(Long id, Persona nuevosDatos){
        Optional<Persona> optionalPersona = personaRepository.findById(id);
        if (optionalPersona.isPresent()){
            Persona persona = optionalPersona.get();

            persona.setTipoDoc(nuevosDatos.getTipoDoc());
            persona.setNumDoc(nuevosDatos.getNumDoc());
            persona.setNombreUno(nuevosDatos.getNombreUno());
            persona.setNombreDos(nuevosDatos.getNombreDos());
            persona.setApellidoUno(nuevosDatos.getApellidoUno());
            persona.setApellidoDos(nuevosDatos.getApellidoDos());
            persona.setTelefono(nuevosDatos.getTelefono());
            persona.setRol(nuevosDatos.getRol());
            persona.setPassword(nuevosDatos.getPassword());
            
            if(nuevosDatos.getPassword() != null && !nuevosDatos.getPassword().isBlank()) {
                //Encriptar solo si se recibe una nueva contraña
                persona.setPassword(passwordEncoder.encode(nuevosDatos.getPassword()));
            }

            return personaRepository.save(persona);
        }

        return null;
    }

    @Override
    public void desactivarPersona(Long id){
        Optional<Persona> optionalPersona = personaRepository.findById(id);
        optionalPersona.ifPresent(personaRepository::delete);
    }
}
