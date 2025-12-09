package com.example.gatekeeper.service;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

@Service
public class ChartGenerator {
    public String generarGraficoBase64(List<String> labels, List<Long> data, String title) throws IOException {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < labels.size(); i++) {
            dataset.addValue(data.get(i), title, labels.get(i));
        }

        JFreeChart chart = ChartFactory.createBarChart(
                title,
                "Etiqueta",
                "Valor",
                dataset
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 800, 500);

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
