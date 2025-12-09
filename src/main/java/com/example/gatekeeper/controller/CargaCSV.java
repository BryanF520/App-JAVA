package com.example.gatekeeper.controller;
import com.example.gatekeeper.Dao.EmpresaDao;
import com.example.gatekeeper.Patrones.EmpresaDesdeArchivoFactory;
import com.example.gatekeeper.entities.Empresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CargaCSV {

   @Autowired
    private EmpresaDao empresaDao;

   //Carga empresas desde un archivo CSV y las guarda en la base de datos
    @PostMapping("/cargarEmpresa")
    public String cargarEmpresa(@RequestParam("file") MultipartFile file, Model model) {
        List<Empresa> nuevas = new ArrayList<>();
        int duplicadas = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String linea;
            boolean primera = true;

            while ((linea = br.readLine()) != null) {
                if (primera) { // saltar cabecera
                    primera = false;
                    continue;
                }

                String[] datos = linea.split(";");
                if (datos.length < 5) {
                    throw new IllegalArgumentException("Formato inválido: faltan columnas en el archivo.");
                }

                long nit;
                try {
                    nit = Long.parseLong(datos[2]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Formato inválido: el NIT no es numérico en alguna fila.");
                }

                Empresa emp = new Empresa();
                emp.setNit(nit);
                emp.setNombre(datos[3]);
                emp.setArea(datos[0]);
                emp.setContacto(datos[1]);
                emp.setUbicacion(datos[4]);

                // verificar duplicados
                if (!empresaDao.existsByNit(emp.getNit())) {
                    nuevas.add(emp);
                } else {
                    duplicadas++;
                }
            }

            // ✅ Si todo salió bien, guardamos en la BD
            for (Empresa e : nuevas) {
                empresaDao.save(e);
            }

            model.addAttribute("mensaje", "Carga completada: " + nuevas.size() +
                    " nuevas, " + duplicadas + " duplicadas.");
            model.addAttribute("empresas", empresaDao.findAll());
            return "empresas/index";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("mensaje", "Error al procesar el archivo: " + e.getMessage());
            model.addAttribute("empresas", empresaDao.findAll()); // mantener las que ya estaban
            return "empresas/index";
        }
    }
}
