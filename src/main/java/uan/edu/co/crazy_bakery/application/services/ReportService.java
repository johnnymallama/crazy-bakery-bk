package uan.edu.co.crazy_bakery.application.services;

import com.itextpdf.text.DocumentException;
import java.io.IOException;

public interface ReportService {
    String generateIngredientAnalysisReport() throws IOException, DocumentException;
    String generateIngredientStrategyReport() throws IOException, DocumentException;
}
