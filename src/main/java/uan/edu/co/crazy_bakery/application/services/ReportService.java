package uan.edu.co.crazy_bakery.application.services;

import com.itextpdf.text.DocumentException;
import java.io.IOException;

public interface ReportService {
    byte[] generateIngredientAnalysisReport() throws IOException, DocumentException;
    byte[] generateIngredientStrategyReport() throws IOException, DocumentException;
}
