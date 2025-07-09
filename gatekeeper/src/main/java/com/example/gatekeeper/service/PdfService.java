package com.example.gatekeeper.service;

import com.example.gatekeeper.entities.Acceso;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final TemplateEngine templateEngine;

    public byte[] generarPdf(List<Acceso> accesos) {

        Context ctx = new Context();
        ctx.setVariable("accesos", accesos);
        String html = templateEngine.process("pdf", ctx);


        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(baos);

            return baos.toByteArray();
        }
        catch (IOException | DocumentException ex) {
            throw new IllegalStateException("Error generando PDF", ex);
        }
    }
}
