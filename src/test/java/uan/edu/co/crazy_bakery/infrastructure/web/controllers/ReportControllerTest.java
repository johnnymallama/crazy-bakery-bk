package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uan.edu.co.crazy_bakery.application.dto.responses.ReporteGeneradoDTO;
import uan.edu.co.crazy_bakery.application.services.ReportService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private static final String STORAGE_URL = "https://firebasestorage.googleapis.com/v0/b/bucket/o/reportes%2Freporte.pdf?alt=media";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateIngredientAnalysisReport_retorna200ConDto() throws IOException, DocumentException {
        // Arrange
        when(reportService.generateIngredientAnalysisReport()).thenReturn(STORAGE_URL);

        // Act
        ResponseEntity<ReporteGeneradoDTO> response = reportController.generateIngredientAnalysisReport();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Análisis de Ingredientes", response.getBody().getNombre_reporte());
        assertNotNull(response.getBody().getFecha_reporte());
        assertEquals(STORAGE_URL, response.getBody().getUrl());
    }

    @Test
    void testGenerateIngredientStrategyReport_retorna200ConDto() throws IOException, DocumentException {
        // Arrange
        when(reportService.generateIngredientStrategyReport()).thenReturn(STORAGE_URL);

        // Act
        ResponseEntity<ReporteGeneradoDTO> response = reportController.generateIngredientStrategyReport();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Estrategia de Ingredientes", response.getBody().getNombre_reporte());
        assertNotNull(response.getBody().getFecha_reporte());
        assertEquals(STORAGE_URL, response.getBody().getUrl());
    }

    @Test
    void testGenerateIngredientAnalysisReport_lanzaIOException() throws IOException, DocumentException {
        // Arrange
        when(reportService.generateIngredientAnalysisReport()).thenThrow(new IOException("Error de lectura"));

        // Act & Assert
        assertThrows(IOException.class, () -> reportController.generateIngredientAnalysisReport());
    }

    @Test
    void testGenerateIngredientStrategyReport_lanzaIOException() throws IOException, DocumentException {
        // Arrange
        when(reportService.generateIngredientStrategyReport()).thenThrow(new IOException("Error de lectura"));

        // Act & Assert
        assertThrows(IOException.class, () -> reportController.generateIngredientStrategyReport());
    }
}
