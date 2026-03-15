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
    private static final Font SUB_SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new BaseColor(0, 51, 102));
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

        if (analysis == null || analysis.trim().isEmpty()) {
            throw new DocumentException("La respuesta de la IA está vacía. No se puede generar el reporte.");
        }

        List<Map<String, Object>> topCombinations = (List<Map<String, Object>>) data.get("combinacionesVendidas");

        return generatePdf(analysis, topCombinations);
    }

    private Map<String, Object> fetchDataForLast50Days() throws JsonProcessingException {
        String topCombinationsSql = "SELECT b.nombre AS bizcocho, r.nombre AS relleno, c.nombre AS cobertura, COUNT(*) AS cantidad_pedidos " +
                "FROM `orden` o INNER JOIN torta t ON o.id = t.id INNER JOIN ingrediente b ON b.id = t.bizcocho_id " +
                "INNER JOIN ingrediente r ON r.id = t.relleno_id INNER JOIN ingrediente c ON c.id = t.cubertura_id " +
                "WHERE o.fecha >= DATE_SUB(CURDATE(), INTERVAL 50 DAY) GROUP BY b.nombre, r.nombre, c.nombre " +
                "ORDER BY cantidad_pedidos DESC LIMIT 5;";

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
        return "Actúa como un analista de datos experto para una pastelería de repostería personalizada.\n"
                + "Usando la base de datos JSON de combinaciones vendidas, genera un reporte estratégico de comportamiento del cliente, cuyo objetivo es apoyar la toma de decisiones operativas mensuales, mejorar la eficiencia del negocio y detectar oportunidades de nuevos productos.\n\n"
                + "---FUENTE DE DATOS---\n" + jsonData + "\n---FIN FUENTE DE DATOS---\n\n"

                + "# Análisis Estratégico de Ingredientes\n\n"
                + "## 1. Resumen Ejecutivo\n"
                + "Redacta un breve resumen explicando:\n- Número total de órdenes analizadas\n- Ingrediente o relleno más popular\n- Combinación más vendida\n- Insight principal del comportamiento del cliente\n- Una recomendación operativa para el negocio\nEl resumen debe ser claro y enfocado en toma de decisiones.\n\n"
                + "## 2. Análisis de Tendencias\n"
                + "Analiza las combinaciones vendidas e identifica:\n- Ingredientes más utilizados\n- Sabores dominantes\n- Preferencias del cliente\nExplica los patrones detectados en un párrafo claro.\n\n"
                + "## 3. Top Demanda\n"
                + "Construye una tabla Markdown con las 5 combinaciones más vendidas. La tabla debe incluir:\n| Combinación | Número de pedidos | Porcentaje de demanda |\n\n"
                + "## 4. Gráfico de Demanda\n"
                + "(El sistema generará el gráfico de barras en esta sección. No es necesario que escribas nada aquí).\n\n\n"
                + "## 5. Propuesta de Tendencias\n"
                + "Analiza los ingredientes más recurrentes del Top Demanda y propone una nueva combinación de postre que podría tener alta aceptación. Explica la propuesta en formato speech o párrafo estratégico, justificando:\n- por qué los ingredientes elegidos son coherentes con las preferencias del cliente\n- cómo esta combinación aporta variedad al menú.\n\n"
                + "## 6. Análisis de Costos\n"
                + "Construye una tabla Markdown comparando la combinación Top Demanda y la nueva combinación propuesta. La tabla debe incluir:\n| Tipo de combinación | Bizcocho | Relleno | Cobertura | Costo estimado por gramo (COP) | Costo por porción (150g) |\nUtiliza valores de costos estimados razonables.\n\n"
                + "## 7. Visualización de Composición\n\n"
                + "### Gráfico 1: Composición y Costo del Postre Top Demanda\n"
                + "(El sistema generará aquí el gráfico de torta. DEBES seguir proveyendo el bloque JSON 'TOP_DEMAND_JSON' con los datos de costo requeridos).\n\n"
                + "### Gráfico 2: Composición y Costo del Postre Propuesto\n"
                + "(El sistema generará aquí el gráfico de torta. DEBES seguir proveyendo el bloque JSON 'PROPUESTA_JSON' con los datos de costo requeridos).\n\n"
                + "## 8. Conclusión Estratégica\n"
                + "Redacta una conclusión breve que explique:\n- qué aprendimos sobre el comportamiento del cliente\n- qué oportunidad de producto existe\n- cómo este análisis puede ayudar a mejorar las decisiones operativas del negocio.\n\n"
                + "--- Reglas generales ---\n"
                + "- Responder en formato Markdown.\n"
                + "- Incluir tablas claras.\n"
                + "- Incluir los gráficos solicitados.\n"
                + "- El análisis debe ser interpretativo y estratégico, no solo descriptivo.\n\n"
                + "--- INSTRUCCIONES CRÍTICAS PARA GRÁFICOS ---\n"
                + "Para que el sistema genere los gráficos de torta, DEBES proporcionar los datos en bloques JSON ocultos separados. Asegúrate de que los nombres de ingredientes y costos sean consistentes con tu análisis en la tabla anterior.\n"
                + "<!-- TOP_DEMAND_JSON\n{\n  \"bizcocho\": \"(nombre bizcocho top)\", \"costo_bizcocho\": (valor),\n  \"relleno\": \"(nombre relleno top)\", \"costo_relleno\": (valor),\n  \"cobertura\": \"(nombre cobertura top)\", \"costo_cobertura\": (valor)\n}\n-->\n\n"
                + "<!-- PROPUESTA_JSON\n{\n  \"bizcocho\": \"(nombre bizcocho propuesto)\", \"costo_bizcocho\": (valor),\n  \"relleno\": \"(nombre relleno propuesto)\", \"costo_relleno\": (valor),\n  \"cobertura\": \"(nombre cobertura propuesto)\", \"costo_cobertura\": (valor)\n}\n-->\n";
    }

    private byte[] generatePdf(String markdownText, List<Map<String, Object>> topCombinations) throws DocumentException, IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();
            addMarkdownContent(document, markdownText, topCombinations);
            document.close();
            return baos.toByteArray();
        }
    }

    private void addMarkdownContent(Document document, String markdownText, List<Map<String, Object>> topCombinations) throws DocumentException, IOException {
        String[] lines = markdownText.split("\n");
        PdfPTable table = null;
        boolean inTable = false;
        boolean skippingJsonBlock = false;

        for (String line : lines) {
            String trimmedLine = line.trim();

            if (trimmedLine.startsWith("<!--")) {
                skippingJsonBlock = true;
            }

            if (skippingJsonBlock) {
                if (trimmedLine.endsWith("-->")) {
                    skippingJsonBlock = false;
                }
                continue;
            }

            if (trimmedLine.isEmpty() || trimmedLine.contains("(El sistema generará")) {
                continue;
            }

            if (trimmedLine.contains("## 4. Gráfico de Demanda")) {
                flushTable(document, table); inTable = false; table = null;
                addParagraph(document, "4. Gráfico de Demanda", SUBTITLE_FONT, Element.ALIGN_LEFT, 15f);
                addBarChart(document, topCombinations);
                document.add(new Paragraph(" ")); // Salto de seguridad
                continue;
            } else if (trimmedLine.contains("### Gráfico 1")) {
                flushTable(document, table); inTable = false; table = null;
                createAndAddPieChart(document, markdownText, "TOP_DEMAND_JSON", "Gráfico 1: Composición y Costo del Postre Top Demanda");
                document.add(new Paragraph(" ")); // Salto de seguridad
                continue; 
            } else if (trimmedLine.contains("### Gráfico 2")) {
                flushTable(document, table); inTable = false; table = null;
                createAndAddPieChart(document, markdownText, "PROPUESTA_JSON", "Gráfico 2: Composición y Costo del Postre Propuesto");
                document.add(new Paragraph(" ")); // Salto de seguridad
                continue;
            }

            if (trimmedLine.startsWith("# ")) {
                flushTable(document, table); inTable = false; table = null;
                addParagraph(document, trimmedLine.substring(2), TITLE_FONT, Element.ALIGN_CENTER, 20f);
            } else if (trimmedLine.startsWith("## ")) {
                flushTable(document, table); inTable = false; table = null;
                if (trimmedLine.startsWith("## 8.")) { // Check if it's section 8
                    document.add(new Paragraph(" ")); // Add the extra break
                }
                addParagraph(document, trimmedLine.substring(3), SUBTITLE_FONT, Element.ALIGN_LEFT, 15f);
            } else if (trimmedLine.startsWith("### ")) {
                flushTable(document, table); inTable = false; table = null;
                addParagraph(document, trimmedLine.substring(4), SUB_SUBTITLE_FONT, Element.ALIGN_LEFT, 15f);
            } else if (trimmedLine.startsWith("|")) {
                if (!inTable) {
                    inTable = true;
                    String[] headers = Arrays.stream(trimmedLine.split("\\|")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
                    table = new PdfPTable(headers.length);
                    table.setWidthPercentage(100);
                    table.setSpacingBefore(10f);
                    table.setSpacingAfter(10f);
                    addTableHeader(table, headers);
                } else if (!trimmedLine.contains("---")) {
                    String[] cells = Arrays.stream(trimmedLine.split("\\|")).map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
                    if (table != null && cells.length == table.getNumberOfColumns()) {
                        addTableRow(table, cells);
                    }
                }
            } else if (trimmedLine.startsWith(">")) {
                flushTable(document, table); inTable = false; table = null;
                addParagraph(document, trimmedLine.substring(1).trim(), NORMAL_FONT, Element.ALIGN_JUSTIFIED, 12f);
            } else {
                flushTable(document, table); inTable = false; table = null;
                addParagraph(document, trimmedLine, NORMAL_FONT, Element.ALIGN_JUSTIFIED, 12f);
            }
        }
        flushTable(document, table);
    }

    private void createAndAddPieChart(Document document, String aiResponse, String jsonBlockId, String chartTitle) throws DocumentException, IOException {
        if (chartTitle != null && !chartTitle.isEmpty()) {
            addParagraph(document, chartTitle, SUB_SUBTITLE_FONT, Element.ALIGN_LEFT, 15f);
        }

        Pattern pattern = Pattern.compile("<!-- " + jsonBlockId + "(.*?)-->", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(aiResponse);

        if (!matcher.find()) {
            document.add(new Paragraph("[Error: No se encontró el bloque de datos JSON '" + jsonBlockId + "' para generar el gráfico.]", NORMAL_FONT));
            return;
        }

        String json = matcher.group(1).trim();
        Map<String, Object> data;
        try {
            data = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            document.add(new Paragraph("[Error al procesar datos JSON para '" + jsonBlockId + "': " + e.getMessage() + "]", NORMAL_FONT));
            return;
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new java.util.Locale("es", "CO"));
        currencyFormat.setMaximumFractionDigits(0);

        double costoBizcocho = ((Number) data.getOrDefault("costo_bizcocho", 0)).doubleValue();
        double costoRelleno = ((Number) data.getOrDefault("costo_relleno", 0)).doubleValue();
        double costoCobertura = ((Number) data.getOrDefault("costo_cobertura", 0)).doubleValue();

        dataset.setValue(String.format("%s - %s", data.getOrDefault("bizcocho", "N/A"), currencyFormat.format(costoBizcocho)), 50);
        dataset.setValue(String.format("%s - %s", data.getOrDefault("relleno", "N/A"), currencyFormat.format(costoRelleno)), 35);
        dataset.setValue(String.format("%s - %s", data.getOrDefault("cobertura", "N/A"), currencyFormat.format(costoCobertura)), 15);

        JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})"));
        plot.setLabelFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 10));
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0}"));

        BufferedImage bufferedImage = chart.createBufferedImage(550, 400);
        Image image = Image.getInstance(bufferedImage, null);
        image.setAlignment(Element.ALIGN_CENTER);
        image.scaleToFit(500, 350);
        document.add(image);
    }

    private void addBarChart(Document document, List<Map<String, Object>> data) throws DocumentException {
        addParagraph(document, "Top 5 Combinaciones Más Demandadas", SUB_SUBTITLE_FONT, Element.ALIGN_CENTER, 10f);

        if (data == null || data.isEmpty()) {
            document.add(new Paragraph("[No se encontraron datos de tendencias para generar el gráfico de barras.]", NORMAL_FONT));
            return;
        }

        double maxValue = data.stream()
                .mapToDouble(row -> ((Number) row.getOrDefault("cantidad_pedidos", 0.0)).doubleValue())
                .max()
                .orElse(1.0);

        PdfPTable chartTable = new PdfPTable(3);
        chartTable.setWidthPercentage(100);
        try { chartTable.setWidths(new float[]{4, 4, 1}); } catch (Exception e) {}
        chartTable.setSpacingBefore(10f);
        chartTable.setSpacingAfter(20f);
        chartTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        chartTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
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
            innerBarCell.setFixedHeight(12f);
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
