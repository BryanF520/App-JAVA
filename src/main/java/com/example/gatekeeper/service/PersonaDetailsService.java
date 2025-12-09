package com.example.gatekeeper.service;

import com.example.gatekeeper.entities.Persona;
import com.example.gatekeeper.repositories.PersonaRepository;
import com.example.gatekeeper.security.PersonaDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonaDetailsService implements UserDetailsService {

    private final PersonaRepository personaRepositry;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Persona persona = personaRepositry.findByNumDoc(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return new PersonaDetails(persona);
    }
    
}
