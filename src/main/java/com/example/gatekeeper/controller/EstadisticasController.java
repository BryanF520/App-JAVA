package com.example.gatekeeper.controller;

import com.example.gatekeeper.entities.Acceso;
import com.example.gatekeeper.entities.Empresa;
import com.example.gatekeeper.repositories.AccesoRepository;
import com.example.gatekeeper.repositories.EmpresaRepository;
import com.example.gatekeeper.service.EstadisticasService;
import com.example.gatekeeper.service.ChartGenerator;
import com.example.gatekeeper.service.PdfService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class EstadisticasController {

    private final AccesoRepository accesoRepository;
    private final EmpresaRepository empresaRepository;
    private final PdfService pdfService;
    private final EstadisticasService estadisticasService;
    private final ChartGenerator chartGenerator;

    @Autowired
    public EstadisticasController(AccesoRepository accesoRepository,
                                EmpresaRepository empresaRepository,
                                PdfService pdfService,
                                EstadisticasService estadisticasService,
                                ChartGenerator chartGenerator) {
        this.accesoRepository = accesoRepository;
        this.empresaRepository = empresaRepository;
        this.pdfService = pdfService;
        this.estadisticasService = estadisticasService;
        this.chartGenerator = chartGenerator;
        }

    @GetMapping("/estadisticas")
    public String index(Model model) {
        List<Acceso> accesos = accesoRepository.findByEstadoTrue();

        // Formateadores
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");

        // --- Agrupar por día (LocalDate -> count) ---
        Map<LocalDate, Long> ingresosPorDia = accesos.stream()
                .filter(a -> a.getFecha_ingreso() != null)
                .collect(Collectors.groupingBy(a -> a.getFecha_ingreso().toLocalDate(), Collectors.counting()));

        List<String> dayLabels = ingresosPorDia.keySet().stream()
                .sorted()
                .map(d -> d.format(dayFormatter))
                .collect(Collectors.toList());

        List<Long> dayData = dayLabels.stream()
                .map(label -> ingresosPorDia.get(LocalDate.parse(label, dayFormatter)))
                .collect(Collectors.toList());

        // --- se agrupa por mes ---
        Map<String, Long> ingresosPorMes = accesos.stream()
                .filter(a -> a.getFecha_ingreso() != null)
                .collect(Collectors.groupingBy(a -> a.getFecha_ingreso().format(monthFormatter), Collectors.counting()));

        List<String> monthLabels = ingresosPorMes.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        List<Long> monthData = monthLabels.stream()
                .map(ingresosPorMes::get)
                .collect(Collectors.toList());

        // --- por año ---
        Map<Integer, Long> ingresosPorAnio = accesos.stream()
                .filter(a -> a.getFecha_ingreso() != null)
                .collect(Collectors.groupingBy(a -> a.getFecha_ingreso().getYear(), Collectors.counting()));

        List<String> yearLabels = ingresosPorAnio.keySet().stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.toList());

        List<Long> yearData = yearLabels.stream()
                .map(y -> ingresosPorAnio.get(Integer.valueOf(y)))
                .collect(Collectors.toList());

        // --- por empresa ---
        List<Empresa> empresas = empresaRepository.findAll();

        List<String> empresaLabels = empresas.stream()
                .map(Empresa::getNombre)
                .collect(Collectors.toList());

        List<Long> empresaData = empresas.stream()
                .map(e -> accesos.stream()
                        .filter(a -> a.getEmpresa() != null && a.getEmpresa().getId() != null && a.getEmpresa().getId().equals(e.getId()))
                        .count())
                .collect(Collectors.toList());

        // --- poner en el modelo ---
        model.addAttribute("dayLabels", dayLabels);
        model.addAttribute("dayData", dayData);

        model.addAttribute("monthLabels", monthLabels);
        model.addAttribute("monthData", monthData);

        model.addAttribute("yearLabels", yearLabels);
        model.addAttribute("yearData", yearData);

        model.addAttribute("empresaLabels", empresaLabels);
        model.addAttribute("empresaData", empresaData);

        return "estadisticas/index";
    }

    @GetMapping("/estadisticas/pdf")
    public void generarPdf(@RequestParam String tipo,HttpServletResponse response) 
    throws IOException {
        
        Map<String, Object> estadisticas = estadisticasService.obtenerEstadisticas(tipo);

        List<String> labels = (List<String>) estadisticas.get("labels");
        List<Long> data = (List<Long>) estadisticas.get("data");
        String title = (String) estadisticas.get("title");
        String datasetLabel = (String) estadisticas.get("datasetLabel");
        String chartType = (String) estadisticas.get("chartType");

        if (labels == null) labels = Collections.emptyList();
        if (data == null) data = Collections.emptyList();

        //  FORZAR LA DESCARGA
        response.setContentType("application/pdf");
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=reporte_" + tipo + ".pdf"
        );

        // Generar imagen del gráfico en Base64
        String chartBase64 = chartGenerator.generarGraficoBase64(labels, data, title);

        // Pasar response + parámetros al PDF
        pdfService.generarPdfEstadisticas(
            response,
            labels,
            data,
            title,
            datasetLabel,
            chartType,
            chartBase64
        );
        }
}