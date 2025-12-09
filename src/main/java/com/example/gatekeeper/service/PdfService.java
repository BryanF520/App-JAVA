package com.example.gatekeeper.service;

import com.example.gatekeeper.entities.Acceso;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

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

    public void generarPdfEstadisticas(
        HttpServletResponse response,
        List<String> labels,
        List<Long> data,
        String title,
        String datasetLabel,
        String chartType,
        String chartBase64
    ) throws IOException {

        Context context = new Context();
        context.setVariable("title", title);
        context.setVariable("labels", labels);
        context.setVariable("data", data);
        context.setVariable("datasetLabel", datasetLabel);
        context.setVariable("chartType", chartType);
        context.setVariable("chartImage", chartBase64);

        // Renderiza HTML Thymeleaf
        String html = templateEngine.process("pdf_estadisticas", context);

        // Convertir a PDF con Flying Saucer o similar
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(baos);
        builder.run();

        // Configurar respuesta HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=estadisticas.pdf");
        response.getOutputStream().write(baos.toByteArray());
    }

    public BufferedImage generarGraficoLinea(List<String> labels, List<Integer> data) {

        XYChart chart = new XYChartBuilder()
            .width(800)
            .height(400)
            .title("Ingresos por Día")
            .xAxisTitle("Fecha")
            .yAxisTitle("Ingresos")
            .build();

        chart.addSeries("Ingresos por Día", labels, data);

        return BitmapEncoder.getBufferedImage(chart);
    }

    public BufferedImage generarGraficoBarras(String titulo, List<String> labels, List<Integer> data) {

        CategoryChart chart = new CategoryChartBuilder()
            .width(800)
            .height(400)
            .title(titulo)
            .xAxisTitle("Periodo")
            .yAxisTitle("Ingresos")
            .build();

        chart.addSeries(titulo, labels, data);

        return BitmapEncoder.getBufferedImage(chart);
    }

    public BufferedImage generarPieChart(List<String> labels, List<Integer> data) {

        PieChart chart = new PieChartBuilder()
            .width(600)
            .height(450)
            .title("Ingresos por Empresa")
            .build();

        for (int i = 0; i < labels.size(); i++) {
            chart.addSeries(labels.get(i), data.get(i));
        }

        return BitmapEncoder.getBufferedImage(chart);
    }

    public byte[] generarPdfEstadisticasConGrafica(String tipo, List<String> labels, List<Integer> data) {

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Reporte de Estadísticas: " + tipo.toUpperCase()));
            document.add(new Paragraph(" "));

            BufferedImage image;

            switch (tipo) {
                case "dia":
                    image = generarGraficoLinea(labels, data);
                    break;

                case "mes":
                    image = generarGraficoBarras("Ingresos por Mes", labels, data);
                    break;

                case "anio":
                    image = generarGraficoBarras("Ingresos por Año", labels, data);
                    break;

                case "empresa":
                    image = generarPieChart(labels, data);
                    break;

                default:
                    throw new IllegalArgumentException("Tipo inválido");
            }

            ByteArrayOutputStream imageBaos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", imageBaos);

            Image pdfImage = Image.getInstance(imageBaos.toByteArray());
            pdfImage.scaleToFit(500, 400);
            document.add(pdfImage);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }

        return baos.toByteArray();
    }
}
