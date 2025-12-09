package com.example.gatekeeper.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import com.example.gatekeeper.repositories.AccesoRepository;
import com.example.gatekeeper.repositories.EmpresaRepository;
import com.example.gatekeeper.entities.Empresa;

@Service
public class EstadisticasService {

    private final AccesoRepository accesoRepository;
    private final EmpresaRepository empresaRepository;

    public EstadisticasService(AccesoRepository accesoRepository, EmpresaRepository empresaRepository) {
        this.accesoRepository = accesoRepository;
        this.empresaRepository = empresaRepository;
    }

    
    // MÉTODO PRINCIPAL
    

    public Map<String, Object> obtenerEstadisticas(String tipo) {

        Map<String, Object> resultado = new HashMap<>();

        switch (tipo.toLowerCase()) {
            case "dia":
                resultado.put("labels", obtenerLabelsDia());
                resultado.put("data", obtenerDataDia());
                resultado.put("title", "Ingresos por Día");
                resultado.put("datasetLabel", "Ingresos");
                resultado.put("chartType", "line");
                break;

            case "mes":
                resultado.put("labels", obtenerLabelsMes());
                resultado.put("data", obtenerDataMes());
                resultado.put("title", "Ingresos por Mes");
                resultado.put("datasetLabel", "Ingresos Mensuales");
                resultado.put("chartType", "bar");
                break;

            case "anio":
                resultado.put("labels", obtenerLabelsAnio());
                resultado.put("data", obtenerDataAnio());
                resultado.put("title", "Ingresos por Año");
                resultado.put("datasetLabel", "Ingresos Anuales");
                resultado.put("chartType", "bar");
                break;

            case "empresa":
                resultado.put("labels", obtenerLabelsEmpresa());
                resultado.put("data", obtenerDataEmpresa());
                resultado.put("title", "Ingresos por Empresa");
                resultado.put("datasetLabel", "Cantidad de Ingresos");
                resultado.put("chartType", "pie");
                break;

            default:
                throw new IllegalArgumentException("Tipo de estadística no válido: " + tipo);
        }

        return resultado;
    }

    
    // ESTADÍSTICAS POR DÍA
    

    public List<String> obtenerLabelsDia() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return accesoRepository.findByEstadoTrue().stream()
                .filter(a -> a.getFecha_ingreso() != null)
                .map(a -> a.getFecha_ingreso().toLocalDate().format(formatter))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Long> obtenerDataDia() {
        return obtenerLabelsDia().stream()
                .map(label -> accesoRepository.countByFechaIngresoDia(LocalDate.parse(label)))
                .collect(Collectors.toList());
    }

    
    // ESTADÍSTICAS POR MES
    

    public List<String> obtenerLabelsMes() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        return accesoRepository.findByEstadoTrue().stream()
                .filter(a -> a.getFecha_ingreso() != null)
                .map(a -> a.getFecha_ingreso().format(formatter))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Long> obtenerDataMes() {
        return obtenerLabelsMes().stream()
                .map(label -> accesoRepository.countByFechaIngresoMes(label))
                .collect(Collectors.toList());
    }

    
    // ESTADÍSTICAS POR AÑO
    

    public List<String> obtenerLabelsAnio() {
        return accesoRepository.findByEstadoTrue().stream()
                .filter(a -> a.getFecha_ingreso() != null)
                .map(a -> String.valueOf(a.getFecha_ingreso().getYear()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Long> obtenerDataAnio() {
        return obtenerLabelsAnio().stream()
                .map(year -> accesoRepository.countByFechaIngresoAnio(Integer.parseInt(year)))
                .collect(Collectors.toList());
    }

    
    // ESTADÍSTICAS POR EMPRESA
    

    public List<String> obtenerLabelsEmpresa() {
        return empresaRepository.findAll()
                .stream()
                .map(Empresa::getNombre)
                .collect(Collectors.toList());
    }

    public List<Long> obtenerDataEmpresa() {
        return empresaRepository.findAll()
                .stream()
                .map(e -> accesoRepository.countByEmpresa(e.getId()))
                .collect(Collectors.toList());
    }
}
