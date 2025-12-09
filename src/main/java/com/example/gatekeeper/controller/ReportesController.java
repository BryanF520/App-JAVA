package com.example.gatekeeper.controller;
import com.example.gatekeeper.entities.Acceso;
import com.example.gatekeeper.entities.Empresa;
import com.example.gatekeeper.service.AccesoService;
import com.example.gatekeeper.service.EmpresaService;
import com.example.gatekeeper.service.PdfService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;


//import com.example.gatekeeper.service.EmailService;

@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReportesController {

    private final AccesoService accesoService;
    private final PdfService pdfService;
    private final EmpresaService empresaService;
//    private final EmailService emailService;

    /* ---------- vista HTML con tabla ---------- */
    @GetMapping
    public String index(@RequestParam(required = false) String nombre,
                        @RequestParam(required = false) String apellido,
                        @RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                        @RequestParam(required = false) Long empresaId,
                        Model model) {
        
         boolean algunParametroEnviado =
            (nombre != null) || (apellido != null) || (fecha != null) || (empresaId != null);

        boolean hayFiltros = (nombre != null && !nombre.isBlank())
            || (apellido != null && !apellido.isBlank())
            || fecha != null
            || empresaId != null;

        List<Acceso> accesos;

        if (!algunParametroEnviado) {
            // Primera carga → NO mostrar nada
            accesos = List.of();
        } else if (!hayFiltros) {
            // Se hizo clic en buscar PERO sin filtros → mostrar todos
            accesos = accesoService.listarAcceso();
        } else {
            // Búsqueda con filtros
            accesos = accesoService.buscar(nombre, apellido, fecha, empresaId);
        };

         boolean descargable =
            algunParametroEnviado      
            && !accesos.isEmpty();
        
        model.addAttribute("accesos", accesos);
        model.addAttribute("empresas", empresaService.listarEmpresa());
        model.addAttribute("empresaId", empresaId);
        model.addAttribute("buscado", hayFiltros);
        model.addAttribute("descargable", descargable);
        return "reportes/index";
    }

    /* ---------- descarga PDF ---------- */
    @GetMapping("/pdf")
    public String descargarPdf(@RequestParam(required = false) String nombre,
                             @RequestParam(required = false) String apellido,
                             @RequestParam(required = false)
                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                             @RequestParam(required = false) Long empresa,
                             RedirectAttributes redirect,
                             HttpServletResponse response) throws IOException {
        
       

        List<Acceso> accesos = accesoService.buscar(nombre, apellido, fecha, empresa);

        if (accesos.isEmpty()) {
            redirect.addFlashAttribute("error", "No hay datos para generar el reporte con los filtros seleccionados.");
            return "redirect:/reportes";
        }

        byte[] pdf = pdfService.generarPdf(accesos);

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=reporte_ingresos.pdf");
        response.setContentLength(pdf.length);

        try (OutputStream os = response.getOutputStream()) {
            os.write(pdf);
            os.flush();
        }
        
        return null;
    }

    
}


