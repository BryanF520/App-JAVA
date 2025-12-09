package com.example.gatekeeper.Patrones;

import com.example.gatekeeper.entities.Persona;
import com.example.gatekeeper.entities.Rol;
import com.example.gatekeeper.entities.Tipo_doc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PersonaDesdeArchivoFactory extends PersonaFactory {

    private final String rutaArchivo;

    public PersonaDesdeArchivoFactory(String rutaArchivo){
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public Persona crear(Tipo_doc tipoDoc, String numDoc, String nombreUno, String nombreDos,
                         String apellidoUno, String apellidoDos, String telefono, Rol rol){

        Persona persona = new Persona();
        persona.setTipoDoc(tipoDoc);
        persona.setNumDoc(numDoc != null && !numDoc.isBlank() ? numDoc : "00000000");
        persona.setNombreUno(nombreUno != null && !nombreUno.isBlank() ? nombreUno : "sinNombre");
        persona.setNombreDos(nombreDos);
        persona.setApellidoUno(apellidoUno != null && !apellidoUno.isBlank() ? apellidoUno : "sinApellido");
        persona.setApellidoDos(apellidoDos);
        persona.setTelefono(telefono != null && !telefono.isBlank() ? telefono : "0000000000");
        persona.setRol(rol);
        return persona;
    }

    //Cargar desde un archivo local
    public List<Persona> crearDesdeCSV(){
        List<Persona> lista = new ArrayList<>();
        try(InputStream is = getClass().getClassLoader().getResourceAsStream(rutaArchivo);
            BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String linea;
            boolean primera = true;

            while ((linea = br.readLine()) != null){
                if (primera){
                    primera = false;
                    continue;
                }

                String[] datos = linea.split(";");
                if (datos.length < 9) continue; //Esto es que espera 9 columnas

                Tipo_doc tipoDoc = new Tipo_doc(); //Se asigna desde BD
                Rol rol = new Rol();

                lista.add(crear(
                        tipoDoc,
                        datos[0],
                        datos[1],
                        datos[2],
                        datos[3],
                        datos[4],
                        datos[5],
                        rol       
                ));
            }
        } catch (IOException | NullPointerException e){
            e.printStackTrace();
        }
        return lista;
    }
}
