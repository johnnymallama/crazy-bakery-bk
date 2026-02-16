package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import com.itextpdf.text.DocumentException;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uan.edu.co.crazy_bakery.application.dto.requests.ReportRequestDTO;
import uan.edu.co.crazy_bakery.application.services.ReportService;

import java.io.IOException;

@RestController
@RequestMapping("/generate-reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<byte[]> generateReport(@Valid @RequestBody ReportRequestDTO requestDTO) throws IOException, DocumentException {
        String reportId = requestDTO.getReportId();

        if ("reporte_estrategico".equals(reportId)) {
            byte[] report = reportService.generateReport(requestDTO);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", reportId + ".pdf");

            return new ResponseEntity<>(report, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
