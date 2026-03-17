package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uan.edu.co.crazy_bakery.application.dto.responses.ReporteGeneradoDTO;
import uan.edu.co.crazy_bakery.application.services.ReportService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Tag(name = "Reportes", description = "Generación de reportes PDF — requiere rol ADMIN")
@RestController
@RequestMapping("/generate-reports")
public class ReportController {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Reporte de análisis de ingredientes", description = "Genera un PDF con el análisis de uso y costos de ingredientes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Metadata del PDF generado en Firebase Storage"),
        @ApiResponse(responseCode = "500", description = "Error al generar el reporte")
    })
    @PostMapping("/ingredient-analysis")
    public ResponseEntity<ReporteGeneradoDTO> generateIngredientAnalysisReport() throws IOException, DocumentException {
        String url = reportService.generateIngredientAnalysisReport();
        return ResponseEntity.ok(new ReporteGeneradoDTO(
            "Análisis de Ingredientes",
            LocalDateTime.now().format(FORMATTER),
            url
        ));
    }

    @Operation(summary = "Reporte de estrategia de ingredientes", description = "Genera un PDF con recomendaciones estratégicas basadas en el consumo de ingredientes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Metadata del PDF generado en Firebase Storage"),
        @ApiResponse(responseCode = "500", description = "Error al generar el reporte")
    })
    @PostMapping("/ingredient-strategy")
    public ResponseEntity<ReporteGeneradoDTO> generateIngredientStrategyReport() throws IOException, DocumentException {
        String url = reportService.generateIngredientStrategyReport();
        return ResponseEntity.ok(new ReporteGeneradoDTO(
            "Estrategia de Ingredientes",
            LocalDateTime.now().format(FORMATTER),
            url
        ));
    }
}
