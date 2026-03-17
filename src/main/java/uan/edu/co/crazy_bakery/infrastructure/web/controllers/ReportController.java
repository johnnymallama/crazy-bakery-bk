package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uan.edu.co.crazy_bakery.application.services.ReportService;

import java.io.IOException;

@Tag(name = "Reportes", description = "Generación de reportes PDF — requiere rol ADMIN")
@RestController
@RequestMapping("/generate-reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Reporte de análisis de ingredientes", description = "Genera un PDF con el análisis de uso y costos de ingredientes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PDF generado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado — se requiere rol ADMIN"),
        @ApiResponse(responseCode = "500", description = "Error al generar el reporte")
    })
    @PostMapping("/ingredient-analysis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generateIngredientAnalysisReport() throws IOException, DocumentException {
        byte[] report = reportService.generateIngredientAnalysisReport();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_analisis_de_ingredientes.pdf");

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }

    @Operation(summary = "Reporte de estrategia de ingredientes", description = "Genera un PDF con recomendaciones estratégicas basadas en el consumo de ingredientes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PDF generado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado — se requiere rol ADMIN"),
        @ApiResponse(responseCode = "500", description = "Error al generar el reporte")
    })
    @PostMapping("/ingredient-strategy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generateIngredientStrategyReport() throws IOException, DocumentException {
        byte[] report = reportService.generateIngredientStrategyReport();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_estrategia_de_ingredientes.pdf");

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }
}
