package com.example.gatekeeper.Patrones;
import com.example.gatekeeper.entities.Persona;
import com.example.gatekeeper.entities.Rol;
import com.example.gatekeeper.entities.Tipo_doc;

public abstract class PersonaFactory {

    public abstract Persona crear(
            Tipo_doc tipoDoc,
            String numDoc,
            String nombreUno,
            String nombreDos,
            String apellidoUno,
            String apellidoDos,
            String telefono,
            Rol rol
    );
}
