package uan.edu.co.crazy_bakery.application.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.jdbc.core.JdbcTemplate;
import uan.edu.co.crazy_bakery.application.services.storage.StorageService;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    // This mock will be initialized as a deep stub in setUp()
    private ChatClient chatClient;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private StorageService storageService;

    private ReportServiceImpl reportService;

    private static final String STORAGE_URL = "https://firebasestorage.googleapis.com/v0/b/bucket/o/reportes%2Freporte.pdf?alt=media";

    @BeforeEach
    void setUp() throws IOException {
        // Use RETURNS_DEEP_STUBS to handle the fluent API chain
        chatClient = Mockito.mock(ChatClient.class, Mockito.RETURNS_DEEP_STUBS);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        Mockito.lenient().when(storageService.uploadBytes(any(byte[].class), anyString(), anyString())).thenReturn(STORAGE_URL);
        reportService = new ReportServiceImpl(chatClientBuilder, jdbcTemplate, new ObjectMapper(), storageService);
    }

    private void mockDatabase() {
        when(jdbcTemplate.queryForList(anyString())).thenReturn(new ArrayList<>());
        when(jdbcTemplate.queryForObject(anyString(), (Class<Long>) any(Class.class))).thenReturn(50L);
    }

    private void mockAiResponse(String content) {
        // With deep stubs, we only need to mock the final call in the chain.
        when(chatClient.prompt().user(anyString()).call().content()).thenReturn(content);
    }

    private String getValidAnalysisMarkdown() {
        return "# Análisis Estratégico\n"
            + "## 1. Resumen Ejecutivo\n"
            + "Resumen de prueba.\n"
            + "## 2. Análisis de Tendencias\n"
            + "Análisis de prueba.\n"
            + "## 3. Top Demanda\n"
            + "| Combinación | Pedidos |\n|---|---|\n| Vainilla + Fresa | 10 |\n"
            + "## 4. Gráfico de Demanda\n"
            + "(placeholder para gráfico)\n"
            + "## 5. Propuesta de Tendencias\n"
            + "Propuesta de prueba.\n"
            + "## 6. Análisis de Costos\n"
            + "| Tipo | Costo |\n|---|---|\n| Top | 100 |\n| Propuesta | 120 |\n"
            + "## 7. Visualización de Composición\n"
            + "### Gráfico 1: Composición y Costo del Postre Top Demanda\n"
            + "(placeholder para gráfico 1)\n"
            + "### Gráfico 2: Composición y Costo del Postre Propuesto\n"
            + "(placeholder para gráfico 2)\n"
            + "## 8. Conclusión Estratégica\n"
            + "Conclusión de prueba.\n"
            + "<!-- TOP_DEMAND_JSON\n{\"bizcocho\":\"Vainilla\",\"costo_bizcocho\":100,\"relleno\":\"Fresa\",\"costo_relleno\":50,\"cobertura\":\"Chocolate\",\"costo_cobertura\":75}-->\n"
            + "<!-- PROPUESTA_JSON\n{\"bizcocho\":\"Chocolate\",\"costo_bizcocho\":120,\"relleno\":\"Arequipe\",\"costo_relleno\":60,\"cobertura\":\"Crema\",\"costo_cobertura\":80}-->";
    }

    private String getValidStrategyMarkdown() {
        return "# Reporte Estratégico\n"
            + "## 1. Resumen Ejecutivo\n"
            + "Resumen de prueba.\n"
            + "## 2. Ingredientes Más Utilizados\n"
            + "(placeholder para tablas)\n"
            + "## 3. Visualización de Uso de Ingredientes\n"
            + "(placeholder para gráficos)\n"
            + "## 4. Ingredientes Poco Utilizados\n"
            + "Análisis de poco utilizados.\n"
            + "## 5. Conclusión Estratégica\n"
            + "Conclusión de prueba.";
    }

    @Test
    @DisplayName("generateIngredientAnalysisReport - Success Case")
    void testGenerateIngredientAnalysisReport_Success() throws IOException, DocumentException {
        // Arrange
        mockDatabase();
        mockAiResponse(getValidAnalysisMarkdown());

        // Act
        String url = reportService.generateIngredientAnalysisReport();

        // Assert
        assertNotNull(url);
        assertFalse(url.isEmpty());
        assertEquals(STORAGE_URL, url);
    }

    @Test
    @DisplayName("generateIngredientStrategyReport - Success Case")
    void testGenerateIngredientStrategyReport_Success() throws IOException, DocumentException {
        // Arrange
        mockDatabase();
        mockAiResponse(getValidStrategyMarkdown());

        // Act
        String url = reportService.generateIngredientStrategyReport();

        // Assert
        assertNotNull(url);
        assertFalse(url.isEmpty());
        assertEquals(STORAGE_URL, url);
    }

    @Test
    @DisplayName("generateIngredientAnalysisReport - Partial AI Response")
    void testGenerateIngredientAnalysisReport_PartialResponse() throws IOException, DocumentException {
        // Arrange
        mockDatabase();
        String partialContent = "# Análisis Parcial\n## 1. Resumen Ejecutivo\nContenido parcial.";
        mockAiResponse(partialContent);

        // Act
        String url = reportService.generateIngredientAnalysisReport();

        // Assert
        assertNotNull(url);
        assertFalse(url.isEmpty());
    }


    @Test
    @DisplayName("generateIngredientAnalysisReport - AI Returns Empty Content")
    void testGenerateAnalysisReport_EmptyAiResponse() {
        // Arrange
        mockDatabase();
        mockAiResponse("");

        // Act & Assert
        Exception exception = assertThrows(DocumentException.class, () -> reportService.generateIngredientAnalysisReport());
        assertEquals("La respuesta de la IA está vacía. No se puede generar el reporte.", exception.getMessage());
    }

    @Test
    @DisplayName("generateIngredientStrategyReport - AI Returns Empty Content")
    void testGenerateStrategyReport_EmptyAiResponse() {
        // Arrange
        mockDatabase();
        mockAiResponse("");

        // Act & Assert
        Exception exception = assertThrows(DocumentException.class, () -> reportService.generateIngredientStrategyReport());
        assertEquals("La respuesta de la IA está vacía. No se puede generar el reporte.", exception.getMessage());
    }
}
