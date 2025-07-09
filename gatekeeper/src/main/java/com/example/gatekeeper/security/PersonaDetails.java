package com.example.gatekeeper.security;

import com.example.gatekeeper.entities.Persona;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class PersonaDetails implements UserDetails{
    private final Persona persona;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + persona.getRol().getRol()));
    }

    @Override
    public String getPassword() {
        return persona.getPassword();
    }

    @Override
    public String getUsername() {
        return persona.getNumDoc();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
