package uan.edu.co.crazy_bakery.application.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.ReportRequestDTO;
import uan.edu.co.crazy_bakery.application.services.ReportService;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Map<String, Object> data = fetchDataForLast50Days();
        String jsonData = objectMapper.writeValueAsString(data);
        String prompt = buildPrompt(jsonData);
        String analysis = chatClient.prompt().user(prompt).call().content();

        List<Map<String, Object>> topCombinations = (List<Map<String, Object>>) data.get("combinacionesVendidas");

        return generatePdf(analysis, topCombinations);
    }

    private Map<String, Object> fetchDataForLast50Days() throws JsonProcessingException {
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
            WHERE o.fecha >= DATE_SUB(CURDATE(), INTERVAL 50 DAY)
            GROUP BY b.nombre, r.nombre, c.nombre
            ORDER BY cantidad_pedidos DESC
            LIMIT 5;
        """;

        String totalOrdersSql = "SELECT COUNT(*) FROM orden WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 50 DAY)";
        String newIngredientsSql = "SELECT nombre, costo_por_gramo FROM ingrediente WHERE tipo_ingrediente NOT IN ('BIZCOCHO', 'RELLENO', 'COBERTURA') AND estado = TRUE;";

        List<Map<String, Object>> topCombinations = jdbcTemplate.queryForList(topCombinationsSql);
        Long totalOrders = jdbcTemplate.queryForObject(totalOrdersSql, Long.class);
        List<Map<String, Object>> newIngredients = jdbcTemplate.queryForList(newIngredientsSql);

        Map<String, Object> data = new HashMap<>();
        data.put("combinacionesVendidas", topCombinations);
        data.put("totalOrdenes", totalOrders != null ? totalOrders : 0L);
        data.put("nuevosIngredientesDisponibles", newIngredients);

        return data;
    }

    private String buildPrompt(String jsonData) {
        return "Actua como un analista de datos y chef de I+D en el sector de la pastelería. Tu tarea es generar un reporte estratégico en formato Markdown a partir de la fuente de datos JSON proporcionada.\n"
                + "El reporte debe seguir estrictamente la estructura y las reglas definidas a continuación. No incluyas explicaciones adicionales ni código HTML.\n\n"
                + "---FUENTE DE DATOS---\n"
                + jsonData + "\n"
                + "---FIN FUENTE DE DATOS---\n\n"
                + "---CONTEXTO DE LA EMPRESA---\n"
                + "El proyecto se enmarca en el sector de repostería personalizada, donde cada producto se diseña a medida.\n"
                + "---FIN CONTEXTO DE LA EMPRESA---\n\n"
                + "---ESTRUCTURA DEL REPORTE---\n"
                + "# ${titulo}\n\n"
                + "## Descripción Reporte\n"
                + "> ${descripcion}\n\n"
                + "## ${contenido}\n\n"
                + "---FIN ESTRUCTURA DEL REPORTE---\n\n"
                + "---REGLAS REPORTE---\n"
                + "1. Titulo = Top ingrediente demanda + Tendencias (Propuesta)\n"
                + "2. descripcion = Crea un speech del resultado del análisis que llevaste.\n"
                + "3. contenido: Debe tener las siguientes secciones en Markdown, en este orden:\n"
                + "   3.1. **Análisis de Tendencias:** Basado en los datos de 'combinacionesVendidas', construye un párrafo de análisis sobre la demanda y las preferencias de los clientes. Menciona que el gráfico de barras del 'Top 5' se adjuntará en el reporte.\n"
                + "   3.2. **Propuesta de Tendencias:** Analiza las combinaciones del Top Demanda para identificar los ingredientes más recurrentes en cada categoría (Bizcocho, Relleno y Cobertura). Con base en ese análisis:\n"
                + "       - Propón una nueva combinación de postre que no exista exactamente en el Top Demanda pero que aproveche los ingredientes más populares.\n"
                + "       - Explica la lógica de la propuesta en formato 'speech' o párrafo, justificando por qué podría tener buena aceptación.\n"
                + "       - **IMPORTANTE**: Al final de tu propuesta, incluye un bloque JSON oculto con la combinación propuesta para que el sistema pueda generar el gráfico. Ejemplo:\n"
                + "         <!-- PROPUESTA_JSON\n{\n  \"bizcocho\": \"Bizcocho de Vainilla\",\n  \"relleno\": \"Crema de Fresa\",\n  \"cobertura\": \"Chocolate Negro\"\n}\n-->\n"
                + "   3.3. **Análisis de Costos:** Construye una tabla Markdown con dos combinaciones de ingredientes (una del top y tu nueva propuesta) y realiza el calculo de los costos por gramo en pesos Colombianos.\n"
                                + "---FIN REGLAS REPORTE---";
    }

    private byte[] generatePdf(String markdownText, List<Map<String, Object>> topCombinations) throws DocumentException, IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // 1. Add all AI-generated text content (analysis, proposal, cost table)
            addMarkdownContent(document, markdownText);

            // 2. Add the historical data bar chart on a new page
            document.newPage();
            addBarChart(document, topCombinations);

            // 3. Add the new proposal pie chart on another new page
            document.newPage();
            addPieChart(document, markdownText);

            document.close();
            return baos.toByteArray();
        }
    }

    private void addMarkdownContent(Document document, String markdownText) throws DocumentException {
        String[] lines = markdownText.split("\n");
        PdfPTable table = null;
        boolean inTable = false;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("<!--")) {
                continue;
            }

            if (line.startsWith("# ")) {
                flushTable(document, table);
                table = null; inTable = false;
                addParagraph(document, line.substring(2), TITLE_FONT, Element.ALIGN_CENTER, 20f);
            } else if (line.startsWith("## ")) {
                flushTable(document, table);
                table = null; inTable = false;
                addParagraph(document, line.substring(3), SUBTITLE_FONT, Element.ALIGN_LEFT, 15f);
            } else if (line.startsWith("|")) {
                if (!inTable) {
                    inTable = true;
                    String[] headers = Arrays.stream(line.split("\\|")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
                    table = new PdfPTable(headers.length);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10f);
                    table.setSpacingAfter(10f);
                    addTableHeader(table, headers);
                } else if (!line.contains("---")) {
                    String[] cells = Arrays.stream(line.split("\\|")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
                    if (cells.length == table.getNumberOfColumns()) {
                        addTableRow(table, cells);
                    }
                }
            } else if (line.startsWith(">")) {
                flushTable(document, table);
                table = null; inTable = false;
                 addParagraph(document, line.substring(1).trim(), NORMAL_FONT, Element.ALIGN_JUSTIFIED, 12f);
            }else {
                flushTable(document, table);
                table = null; inTable = false;
                addParagraph(document, line, NORMAL_FONT, Element.ALIGN_JUSTIFIED, 12f);
            }
        }
        flushTable(document, table);
    }

    private void addPieChart(Document document, String aiResponse) throws DocumentException, IOException {
        addParagraph(document, "Composición del Postre Propuesto", SUBTITLE_FONT, Element.ALIGN_CENTER, 20f);

        Pattern pattern = Pattern.compile("<!-- PROPUESTA_JSON(.*?)-->", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(aiResponse);

        if (!matcher.find()) {
            document.add(new Paragraph("No se pudo encontrar la propuesta de postre en la respuesta de la IA.", NORMAL_FONT));
            return;
        }

        String json = matcher.group(1).trim();
        Map<String, String> proposal;
        try {
            proposal = objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            document.add(new Paragraph("Error al procesar la propuesta de postre (JSON mal formado): " + e.getMessage(), NORMAL_FONT));
            return;
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue(String.format("%s (Bizcocho)", proposal.getOrDefault("bizcocho", "N/A")), 50);
        dataset.setValue(String.format("%s (Relleno)", proposal.getOrDefault("relleno", "N/A")), 35);
        dataset.setValue(String.format("%s (Cobertura)", proposal.getOrDefault("cobertura", "N/A")), 15);

        JFreeChart chart = ChartFactory.createPieChart(
                null, 
                dataset,
                true, 
                true,
                false);

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{2}", NumberFormat.getPercentInstance(), NumberFormat.getPercentInstance()));
        plot.setLabelFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0}"));


        BufferedImage bufferedImage = chart.createBufferedImage(550, 400);
        Image image = Image.getInstance(bufferedImage, null);
        image.setAlignment(Element.ALIGN_CENTER);
        image.scaleToFit(500, 350);
        image.setSpacingBefore(10f);
        document.add(image);
    }

    private void addBarChart(Document document, List<Map<String, Object>> data) throws DocumentException {
        addParagraph(document, "Top 5 Combinaciones Más Demandadas", SUBTITLE_FONT, Element.ALIGN_CENTER, 20f);

        if (data == null || data.isEmpty()) {
            document.add(new Paragraph("No se encontraron datos de tendencias para generar el gráfico de barras.", NORMAL_FONT));
            return;
        }

        double maxValue = data.stream()
                .mapToDouble(row -> ((Number) row.getOrDefault("cantidad_pedidos", 0.0)).doubleValue())
                .max()
                .orElse(1.0);

        PdfPTable chartTable = new PdfPTable(3);
        chartTable.setWidthPercentage(100);
        chartTable.setWidths(new float[]{4, 4, 1});
        chartTable.setSpacingBefore(15f);
        chartTable.setSpacingAfter(25f);
        chartTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        chartTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        BaseColor barColor = new BaseColor(70, 130, 180);

        for (Map<String, Object> row : data) {
            String label = Stream.of((String) row.get("bizcocho"), (String) row.get("relleno"), (String) row.get("cobertura"))
                                 .filter(s -> s != null && !s.isEmpty())
                                 .collect(Collectors.joining(" + "));

            double value = ((Number) row.getOrDefault("cantidad_pedidos", 0.0)).doubleValue();
            String valueText = String.format("%.0f", value);

            PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
            labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            labelCell.setPaddingRight(10);
            labelCell.setBorder(Rectangle.NO_BORDER);
            labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            chartTable.addCell(labelCell);

            float barWidthPercentage = (float) ((value / maxValue) * 100.0);
            PdfPTable barWrapperTable = new PdfPTable(1);
            barWrapperTable.setWidthPercentage(barWidthPercentage > 0 ? barWidthPercentage : 1);
            barWrapperTable.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell innerBarCell = new PdfPCell(new Phrase(" "));
            innerBarCell.setFixedHeight(15f);
            innerBarCell.setBackgroundColor(barColor);
            innerBarCell.setBorder(Rectangle.NO_BORDER);
            barWrapperTable.addCell(innerBarCell);

            PdfPCell barWrapperCell = new PdfPCell();
            barWrapperCell.addElement(barWrapperTable);
            barWrapperCell.setBorder(Rectangle.NO_BORDER);
            barWrapperCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            chartTable.addCell(barWrapperCell);

            PdfPCell valueCell = new PdfPCell(new Phrase(valueText, labelFont));
            valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            valueCell.setPaddingLeft(5);
            valueCell.setBorder(Rectangle.NO_BORDER);
            valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            chartTable.addCell(valueCell);
        }
        document.add(chartTable);
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
