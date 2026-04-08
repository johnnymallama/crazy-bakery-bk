package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uan.edu.co.crazy_bakery.application.services.ReportService;
import uan.edu.co.crazy_bakery.infrastructure.web.security.FirebaseTokenFilter;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ReportController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FirebaseTokenFilter.class)
)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    private static final String STORAGE_URL = "https://firebasestorage.googleapis.com/v0/b/bucket/o/reportes%2Freporte.pdf?alt=media";

    @Test
    void generateIngredientAnalysisReport_Success() throws Exception {
        when(reportService.generateIngredientAnalysisReport()).thenReturn(STORAGE_URL);

        mockMvc.perform(post("/generate-reports/ingredient-analysis"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre_reporte").value("Análisis de Ingredientes"))
                .andExpect(jsonPath("$.fecha_reporte").isNotEmpty())
                .andExpect(jsonPath("$.url").value(STORAGE_URL));
    }

    @Test
    void generateIngredientStrategyReport_Success() throws Exception {
        when(reportService.generateIngredientStrategyReport()).thenReturn(STORAGE_URL);

        mockMvc.perform(post("/generate-reports/ingredient-strategy"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre_reporte").value("Estrategia de Ingredientes"))
                .andExpect(jsonPath("$.fecha_reporte").isNotEmpty())
                .andExpect(jsonPath("$.url").value(STORAGE_URL));
    }

    @Test
    void generateIngredientAnalysisReport_ThrowsIOException() throws Exception {
        when(reportService.generateIngredientAnalysisReport()).thenThrow(new IOException("Error de lectura"));

        mockMvc.perform(post("/generate-reports/ingredient-analysis"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void generateIngredientStrategyReport_ThrowsIOException() throws Exception {
        when(reportService.generateIngredientStrategyReport()).thenThrow(new IOException("Error de lectura"));

        mockMvc.perform(post("/generate-reports/ingredient-strategy"))
                .andExpect(status().isInternalServerError());
    }
}
