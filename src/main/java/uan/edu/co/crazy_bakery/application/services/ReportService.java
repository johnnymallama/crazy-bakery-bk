package uan.edu.co.crazy_bakery.application.services;

import com.itextpdf.text.DocumentException;
import uan.edu.co.crazy_bakery.application.dto.requests.ReportRequestDTO;

import java.io.IOException;

public interface ReportService {

    byte[] generateReport(ReportRequestDTO requestDTO) throws IOException, DocumentException;

}
