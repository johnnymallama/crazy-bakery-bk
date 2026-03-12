package uan.edu.co.crazy_bakery.application.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.jdbc.core.JdbcTemplate;
import uan.edu.co.crazy_bakery.application.dto.requests.ReportRequestDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ReportServiceImplTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec chatClientRequestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        reportService = new ReportServiceImpl(chatClientBuilder, jdbcTemplate, new ObjectMapper());
    }

    @Test
    void testGenerateReport_retornaByteArrayNoVacio() throws IOException, DocumentException {
        // Arrange
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        requestDTO.setReportId("reporte_estrategico");
        requestDTO.setStartDate(LocalDate.now().minusDays(7));
        requestDTO.setEndDate(LocalDate.now());

        // Mock jdbcTemplate para las dos consultas SQL
        List<Map<String, Object>> topCombinations = Collections.singletonList(
                Map.of("bizcocho", "Vainilla", "relleno", "Fresa", "cobertura", "Chocolate", "cantidad_pedidos", 5)
        );
        List<Map<String, Object>> newIngredients = Collections.singletonList(
                Map.of("nombre", "Canela", "costo_por_gramo", 100.0)
        );

        when(jdbcTemplate.queryForList(anyString()))
                .thenReturn(topCombinations)
                .thenReturn(newIngredients);

        // Mock ChatClient chain
        String markdownContent = "# Top Ingredientes\n\n## Descripción Reporte.\n> Análisis estratégico.\n\n## Contenido\n\n| Bizcocho | Relleno | Cobertura | Cantidad |\n|---|---|---|---|\n| Vainilla | Fresa | Chocolate | 5 |\n";
        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.user(anyString())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(markdownContent);

        // Act
        byte[] result = reportService.generateReport(requestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testGenerateReport_conMarkdownSencillo_retornaByteArrayNoVacio() throws IOException, DocumentException {
        // Arrange
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        requestDTO.setReportId("reporte_estrategico");

        // Mock jdbcTemplate
        when(jdbcTemplate.queryForList(anyString()))
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList());

        // Mock ChatClient con markdown sencillo (sin tablas)
        String markdownContent = "# Título del Reporte\n\n## Sección 1\n\nEste es un párrafo normal del análisis.\n\n## Sección 2\n\nOtro párrafo con ```codigo``` que debe ignorarse.\n";
        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.user(anyString())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(markdownContent);

        // Act
        byte[] result = reportService.generateReport(requestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testGenerateReport_conLineasEspeciales_retornaByteArrayNoVacio() throws IOException, DocumentException {
        // Arrange: cubre ramas de líneas vacías, ---INICIO/---FIN, y tabla con separador ---
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        requestDTO.setReportId("reporte_estrategico");

        when(jdbcTemplate.queryForList(anyString()))
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList());

        // Markdown con líneas especiales para cubrir todas las ramas del parser
        String markdownContent = "---INICIO seccion---\n"
                + "\n"
                + "---FIN seccion---\n"
                + "# Reporte con Tabla\n"
                + "| Col1 | Col2 |\n"
                + "|------|------|\n"
                + "| val1 | val2 |\n"
                + "## Subtítulo\n"
                + "Párrafo después de tabla.\n";

        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.user(anyString())).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.content()).thenReturn(markdownContent);

        // Act
        byte[] result = reportService.generateReport(requestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
