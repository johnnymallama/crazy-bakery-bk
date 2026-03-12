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
import uan.edu.co.crazy_bakery.application.dto.requests.ReportRequestDTO;
import uan.edu.co.crazy_bakery.application.services.ReportService;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    void testGenerateReport_conReporteEstrategico_retorna200ConPdf() throws IOException, DocumentException {
        // Arrange
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        requestDTO.setReportId("reporte_estrategico");
        requestDTO.setStartDate(LocalDate.now().minusDays(7));
        requestDTO.setEndDate(LocalDate.now());

        byte[] pdfBytes = new byte[]{37, 80, 68, 70}; // cabecera mínima %PDF
        when(reportService.generateReport(any(ReportRequestDTO.class))).thenReturn(pdfBytes);

        // Act
        ResponseEntity<byte[]> response = reportController.generateReport(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getContentDisposition().toString().contains("reporte_estrategico.pdf"));
    }

    @Test
    void testGenerateReport_conReporteDesconocido_retorna400() throws IOException, DocumentException {
        // Arrange
        ReportRequestDTO requestDTO = new ReportRequestDTO();
        requestDTO.setReportId("reporte_inexistente");

        // Act
        ResponseEntity<byte[]> response = reportController.generateReport(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }
}
