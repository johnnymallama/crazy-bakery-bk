package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import uan.edu.co.crazy_bakery.application.services.ReportService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateIngredientAnalysisReport_retorna200ConPdf() throws IOException, DocumentException {
        // Arrange
        byte[] pdfBytes = new byte[]{37, 80, 68, 70}; // cabecera mínima %PDF
        when(reportService.generateIngredientAnalysisReport()).thenReturn(pdfBytes);

        // Act
        ResponseEntity<byte[]> response = reportController.generateIngredientAnalysisReport();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getContentDisposition().toString().contains("reporte_analisis_de_ingredientes.pdf"));
    }

    @Test
    void testGenerateIngredientStrategyReport_retorna200ConPdf() throws IOException, DocumentException {
        // Arrange
        byte[] pdfBytes = new byte[]{37, 80, 68, 70}; // cabecera mínima %PDF
        when(reportService.generateIngredientStrategyReport()).thenReturn(pdfBytes);

        // Act
        ResponseEntity<byte[]> response = reportController.generateIngredientStrategyReport();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getContentDisposition().toString().contains("reporte_estrategia_de_ingredientes.pdf"));
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
