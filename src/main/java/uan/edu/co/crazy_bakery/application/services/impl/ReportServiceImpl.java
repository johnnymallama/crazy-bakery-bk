package uan.edu.co.crazy_bakery.application.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.ReportRequestDTO;
import uan.edu.co.crazy_bakery.application.services.ReportService;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private final ChatClient chatClient;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new BaseColor(0, 51, 102));
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);

    public ReportServiceImpl(ChatClient.Builder chatClientBuilder, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] generateReport(ReportRequestDTO requestDTO) throws IOException, DocumentException {
        // Set headless mode for AWT to work in a containerized environment
        System.setProperty("java.awt.headless", "true");

        Map<String, Object> data = fetchDataForLast7Days();
        String jsonData = objectMapper.writeValueAsString(data);
        String prompt = buildPrompt(jsonData);
        String analysis = chatClient.prompt().user(prompt).call().content();

        List<Map<String, Object>> chartData = (List<Map<String, Object>>) data.get("combinacionesVendidas");

        return generatePdfFromMarkdown(analysis, chartData);
    }

    private Map<String, Object> fetchDataForLast7Days() throws JsonProcessingException {
        String topCombinationsSql = """
            SELECT
                b.nombre AS bizcocho,
                r.nombre AS relleno,
                c.nombre AS cobertura,
                COUNT(*) AS cantidad_pedidos
            FROM `orden` o
            INNER JOIN torta t ON o.id = t.id
            INNER JOIN ingrediente b ON b.id = t.bizcocho_id
            INNER JOIN ingrediente r ON r.id = t.relleno_id
            INNER JOIN ingrediente c ON c.id = t.cubertura_id
            WHERE o.fecha >= DATE_SUB(CURDATE(), INTERVAL 20 DAY)
            GROUP BY b.nombre, r.nombre, c.nombre
            ORDER BY cantidad_pedidos DESC
            LIMIT 5;
        """;

        String newIngredientsSql = "SELECT nombre, costo_por_gramo FROM ingrediente WHERE tipo_ingrediente NOT IN ('BIZCOCHO', 'RELLENO', 'COBERTURA') AND estado = TRUE;";

        List<Map<String, Object>> topCombinations = jdbcTemplate.queryForList(topCombinationsSql);
        List<Map<String, Object>> newIngredients = jdbcTemplate.queryForList(newIngredientsSql);

        Map<String, Object> data = new HashMap<>();
        data.put("combinacionesVendidas", topCombinations);
        data.put("nuevosIngredientesDisponibles", newIngredients);

        return data;
    }

    private String buildPrompt(String jsonData) {
        return """
        Eres un asistente de análisis de datos para una pastelería. Tu tarea es generar un reporte en formato Markdown a partir de los datos JSON proporcionados.
        Completa cada una de las siguientes secciones como se indica, rellenando el contenido donde se solicita.

        ---DATOS JSON---
        ```json
        %s
        ```
        ---FIN DATOS JSON---

        ---INICIO DEL REPORTE---

        # Análisis Estratégico de Ingredientes (Últimos 7 Días)

        ## Top Demanda

        **Instrucción:** Crea una tabla Markdown que muestre las 5 combinaciones más pedidas de la sección `combinacionesVendidas`. La tabla debe tener las columnas: "Bizcocho", "Relleno", "Cobertura" y "Cantidad de Pedidos".
        (Aquí va la tabla Markdown de Top Demanda)

        ## Gráfico de Demanda

        **Instrucción:** Esta sección se genera automáticamente por el sistema. No escribas absolutamente nada aquí.

        ## Propuesta de Tendencias

        **Instrucción:** Basado en los datos de `combinacionesVendidas`, redacta un párrafo analizando las tendencias. ¿Qué sabores son populares? ¿Qué sugiere esto sobre las preferencias del cliente?
        (Aquí va el párrafo de análisis de tendencias)

        ## Análisis de Costos

        **Instrucción:** Usando la lista de `nuevosIngredientesDisponibles`, propón 2 nuevas combinaciones de tortas. Crea una tabla Markdown con las columnas "Nueva Combinación Propuesta" y "Justificación de la Propuesta".
        (Aquí va la tabla de análisis de costos)

        ---FIN DEL REPORTE---
        """.formatted(jsonData);
    }

    private byte[] generatePdfFromMarkdown(String markdownText, List<Map<String, Object>> chartData) throws DocumentException, IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            boolean chartGenerated = false;
            String[] lines = markdownText.split("\n");
            PdfPTable table = null;
            boolean inTable = false;

            for (String line : lines) {
                line = line.trim();
                 if (line.isEmpty() || line.contains("(Aquí va") || line.startsWith("---INICIO") || line.startsWith("---FIN") || line.startsWith("**Instrucción:**")) {
                    continue;
                }

                if (line.startsWith("## Gráfico de Demanda") && !chartGenerated) {
                    flushTable(document, table);
                    table = null;
                    inTable = false;
                    addParagraph(document, "Gráfico de Demanda", SUBTITLE_FONT, Element.ALIGN_LEFT, 15f);

                    byte[] chartBytes = createChartImage(chartData);
                    Image chartImage = Image.getInstance(chartBytes);
                    chartImage.scaleToFit(500, 400);
                    chartImage.setAlignment(Element.ALIGN_CENTER);
                    document.add(chartImage);
                    chartGenerated = true;
                } else if (line.startsWith("# ")) {
                    flushTable(document, table);
                    table = null;
                    inTable = false;
                    addParagraph(document, line.substring(2), TITLE_FONT, Element.ALIGN_CENTER, 20f);
                } else if (line.startsWith("## ")) {
                    flushTable(document, table);
                    table = null;
                    inTable = false;
                    // Evita que se duplique el título del gráfico
                    if (!line.contains("Gráfico de Demanda")) {
                         addParagraph(document, line.substring(3), SUBTITLE_FONT, Element.ALIGN_LEFT, 15f);
                    }
                } else if (line.startsWith("|")) {
                    if (!inTable) {
                        inTable = true;
                        table = new PdfPTable(parseTableLine(line).length);
                        table.setWidthPercentage(100);
                        table.setSpacingBefore(10f);
                        table.setSpacingAfter(10f);
                        addTableHeader(table, parseTableLine(line));
                    } else if (!line.contains("---")) {
                        addTableRow(table, parseTableLine(line));
                    }
                } else if (!line.startsWith("```")) { // Ignore code block fences
                    flushTable(document, table);
                    table = null;
                    inTable = false;
                    addParagraph(document, line, NORMAL_FONT, Element.ALIGN_JUSTIFIED, 12f);
                }
            }
            flushTable(document, table);
            document.close();
            return baos.toByteArray();
        }
    }

    private byte[] createChartImage(List<Map<String, Object>> data) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map<String, Object> item : data) {
            String bizcocho = (String) item.getOrDefault("bizcocho", "");
            String relleno = (String) item.getOrDefault("relleno", "");
            String cobertura = (String) item.getOrDefault("cobertura", "");
            String combination = bizcocho + ", " + relleno + ", " + cobertura;
            Number value = (Number) item.get("cantidad_pedidos");
            dataset.addValue(value, "Frecuencia", combination);
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Top 5 Combinaciones Más Vendidas",
                "Combinación",
                "Frecuencia",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.getRenderer().setSeriesPaint(0, new Color(0, 51, 102));
        plot.setBackgroundPaint(Color.white);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(0.05);

        // Aumentar la calidad de la imagen
        return ChartUtils.encodeAsPNG(barChart.createBufferedImage(800, 600));
    }

    private void flushTable(Document document, PdfPTable table) throws DocumentException {
        if (table != null) {
            document.add(table);
        }
    }

    private void addParagraph(Document document, String text, Font font, int alignment, float spacingAfter) throws DocumentException {
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(alignment);
        p.setSpacingAfter(spacingAfter);
        document.add(p);
    }

    private String[] parseTableLine(String line) {
        return Arrays.stream(line.split("\\|")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
    }

    private void addTableHeader(PdfPTable table, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(new BaseColor(0, 51, 102));
            cell.setPhrase(new Phrase(header, TABLE_HEADER_FONT));
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void addTableRow(PdfPTable table, String[] cells) {
        for (String cellText : cells) {
            PdfPCell cell = new PdfPCell(new Phrase(cellText, NORMAL_FONT));
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }
}
