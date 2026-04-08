package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uan.edu.co.crazy_bakery.application.dto.requests.CustomCakeImageRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.IngredientDetailDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.GeneratedImageResponseDTO;
import uan.edu.co.crazy_bakery.application.services.ImageGenerationService;
import uan.edu.co.crazy_bakery.infrastructure.web.security.FirebaseTokenFilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ImageGenerationController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FirebaseTokenFilter.class)
)
class ImageGenerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageGenerationService imageGenerationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void generateImage_WithValidPrompt_ReturnsImageUrl() throws Exception {
        String prompt = "a beautiful cake";
        String expectedUrl = "http://example.com/cake.png";
        Map<String, String> request = Map.of("prompt", prompt);

        when(imageGenerationService.generateImage(eq(prompt))).thenReturn(expectedUrl);

        mockMvc.perform(post("/generate-image")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.imageUrl").value(expectedUrl));
    }

    @Test
    void generateImage_WithEmptyPrompt_ReturnsBadRequest() throws Exception {
        Map<String, String> request = Map.of("prompt", " ");

        mockMvc.perform(post("/generate-image")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    void generateImage_WithNullPrompt_ReturnsBadRequest() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("otra_clave", "valor");

        mockMvc.perform(post("/generate-image")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generateCustomCakeImage_WithValidRequest_ReturnsGeneratedImage() throws Exception {
        CustomCakeImageRequestDTO requestDTO = new CustomCakeImageRequestDTO(
                "CUPCAKE",
                "Standard size",
                Collections.singletonList(new IngredientDetailDTO("BIZCOCHO", "Vanilla", "Vanilla extract")),
                "For a small party"
        );
        GeneratedImageResponseDTO responseDTO = new GeneratedImageResponseDTO("Generated prompt", "http://example.com/cupcake.png");

        when(imageGenerationService.generateCustomCakeImage(any(CustomCakeImageRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/generate-image/custom-cake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.prompt").value("Generated prompt"))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/cupcake.png"));
    }

    @Test
    void generateCustomCakeImage_WithNullTipoReceta_ReturnsBadRequest() throws Exception {
        CustomCakeImageRequestDTO requestDTO = new CustomCakeImageRequestDTO(
                null,
                "Standard size",
                Collections.singletonList(new IngredientDetailDTO("BIZCOCHO", "Vanilla", "Vanilla extract")),
                "For a small party"
        );

        mockMvc.perform(post("/generate-image/custom-cake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void generateCustomCakeImage_WithEmptyTipoReceta_ReturnsBadRequest() throws Exception {
        CustomCakeImageRequestDTO requestDTO = new CustomCakeImageRequestDTO(
                "",
                "Standard size",
                Collections.singletonList(new IngredientDetailDTO("BIZCOCHO", "Vanilla", "Vanilla extract")),
                "For a small party"
        );

        mockMvc.perform(post("/generate-image/custom-cake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }
}
