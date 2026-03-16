package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import com.itextpdf.text.DocumentException;
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

@RestController
@RequestMapping("/generate-reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/ingredient-analysis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generateIngredientAnalysisReport() throws IOException, DocumentException {
        byte[] report = reportService.generateIngredientAnalysisReport();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_analisis_de_ingredientes.pdf");

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }

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
