package com.example.gatekeeper.Patrones;

import com.example.gatekeeper.entities.Empresa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDesdeArchivoFactory extends EmpresaFactory{

    private final String rutaArchivo;

    public EmpresaDesdeArchivoFactory(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    // Se encarga de crear una Empresa con validaciones de valores nulos o vacíos.
    @Override
    public Empresa crear(String area, String contacto, Long nit, String nombre, String ubicacion){
        Empresa empresa = new Empresa();
        empresa.setArea(area != null && !area.isBlank() ? area:"sinNombre");
        empresa.setContacto(contacto != null && !contacto.isBlank() ? contacto:"0000000000");
        empresa.setNit(nit > 0 ? nit : 0L);
        empresa.setNombre(nombre != null && !nombre.isBlank() ? nombre:"sinNombre");
        empresa.setUbicacion(ubicacion != null && !ubicacion.isBlank() ? ubicacion:"sinUbicacion");
        return empresa;
    }

    //Método para leer el archivo CSV y crear una lista de empresas a partir de su contenido
    public List<Empresa> crearDesdeCSV(){
        List<Empresa> lista = new ArrayList<>();
        try(InputStream is = getClass().getClassLoader().getResourceAsStream(rutaArchivo);
            BufferedReader br=new BufferedReader(new InputStreamReader(is))){

            String linea;
            boolean primera=true;
            while ((linea=br.readLine())!= null){
                if (primera){
                    primera=false;
                    continue;
                }
                String[] datos=linea.split(";");
                if (datos.length >= 5){
                    long nit = Long.parseLong(datos[2]);
                    lista.add(crear(datos[0],datos[1],nit,datos[3],datos[4]));
                }
            }
        }catch (IOException | NullPointerException e){
            e.printStackTrace();
        }
        return lista;
    }
}
