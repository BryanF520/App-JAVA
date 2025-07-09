package com.example.gatekeeper.controller;

import com.example.gatekeeper.entities.Acceso;
import com.example.gatekeeper.service.AccesoService;
import com.example.gatekeeper.service.PdfService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReportesController {

    private final AccesoService accesoService;
    private final PdfService pdfService;

    /* ---------- vista HTML con tabla ---------- */
    @GetMapping
    public String index(@RequestParam(required = false) String nombre,
                        @RequestParam(required = false) String apellido,
                        @RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                        @RequestParam(required = false) String empresa,
                        Model model) {

        boolean hayFiltros = nombre != null || apellido != null
                || fecha != null  || empresa  != null;

        List<Acceso> accesos = hayFiltros
                ? accesoService.buscar(nombre, apellido, fecha, empresa)
                : List.of();

        model.addAttribute("accesos", accesos);
        return "reportes/index";
    }

    /* ---------- descarga PDF ---------- */
    @GetMapping("/pdf")
    public void descargarPdf(@RequestParam(required = false) String nombre,
                             @RequestParam(required = false) String apellido,
                             @RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                             @RequestParam(required = false) String empresa,
                             HttpServletResponse response) throws IOException {

        List<Acceso> accesos = accesoService.buscar(nombre, apellido, fecha, empresa);
        byte[] pdf = pdfService.generarPdf(accesos);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=reporte_ingresos.pdf");
        response.setContentLength(pdf.length);

        try (OutputStream os = response.getOutputStream()) {
            os.write(pdf);
            os.flush();
        }
    }
}
