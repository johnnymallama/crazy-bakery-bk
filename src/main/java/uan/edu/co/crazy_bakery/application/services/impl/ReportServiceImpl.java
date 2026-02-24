package uan.edu.co.crazy_bakery.application.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.ReportRequestDTO;
import uan.edu.co.crazy_bakery.application.services.ReportService;

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
        Map<String, Object> data = fetchDataForLast7Days();
        String jsonData = objectMapper.writeValueAsString(data);
        String prompt = buildPrompt(jsonData);
        String analysis = chatClient.prompt().user(prompt).call().content();

        return generatePdfFromMarkdown(analysis);
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
            WHERE o.fecha >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
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
        String promptTemplate = "Actua como un analista de datos experto en el sector de la pastelería. Tu tarea es generar un reporte estratégico en formato Markdown a partir de la fuente de datos JSON proporcionada.\n"
            + "El reporte debe seguir estrictamente la estructura y las reglas definidas a continuación. No incluyas explicaciones adicionales ni código HTML. Genera únicamente el contenido del reporte en Markdown.\n\n"
            + "---FUENTE DE DATOS---\n"
            + "%s\n"
            + "---FIN FUENTE DE DATOS---\n\n"
            + "---CONTEXTO DE LA EMPRESA---\n"
            + "El proyecto se enmarca en el sector de repostería personalizada, donde cada producto se diseña a medida.\n"
            + "---FIN CONTEXTO DE LA EMPRESA---\n\n"
            + "---ESTRUCTURA DEL REPORTE---\n"
            + "# ${titulo}\n\n"
            + "## Descripción Reporte.\n"
            + "> ${descripcion}\n\n"
            + "## ${contenido}\n"
            + "---FIN ESTRUCTURA DEL REPORTE---\n\n"
            + "---REGLAS REPORTE---\n"
            + "1. Titulo = Top ingrediente demanda + Tendencias (Propuesta)\n"
            + "2. drescripcion = Crea un speach del resultado del analisis que llevaste\n"
            + "3. contenido:\n"
            + "3.1. Top Demanda = Crea una tabla Markdown con las 5 combinaciones de ingredientes con mayor demanda.\n"
            + "3.2. Propuesta de tendencia = Construye un parrafo que proponga una nueva tendencia de combinacion de ingredientes apoyado en el Top Demanda y las tendencias actuales de pasteleria en Colombia.\n"
            + "3.3. Analisis de costos = Construye una tabla Markdown con dos combinaciones de ingredientes según la tendencia anterior y realiza el calculo de los costos por gramo en pesos Colombianos.\n"
            + "---FIN REGLAS REPORTE---";
        return String.format(promptTemplate, jsonData);
    }

    private byte[] generatePdfFromMarkdown(String markdownText) throws DocumentException, IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            String[] lines = markdownText.split("\n");
            PdfPTable table = null;
            boolean inTable = false;

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("---INICIO") || line.startsWith("---FIN")) {
                    continue;
                }

                if (line.startsWith("# ")) {
                    flushTable(document, table);
                    table = null;
                    inTable = false;
                    addParagraph(document, line.substring(2), TITLE_FONT, Element.ALIGN_CENTER, 20f);
                } else if (line.startsWith("## ")) {
                    flushTable(document, table);
                    table = null;
                    inTable = false;
                    addParagraph(document, line.substring(3), SUBTITLE_FONT, Element.ALIGN_LEFT, 15f);
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
