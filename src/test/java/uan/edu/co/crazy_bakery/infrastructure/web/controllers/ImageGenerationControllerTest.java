package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uan.edu.co.crazy_bakery.application.dto.requests.CustomCakeImageRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.IngredientDetailDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.GeneratedImageResponseDTO;
import uan.edu.co.crazy_bakery.application.services.ImageGenerationService;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ImageGenerationControllerTest {

    @Mock
    private ImageGenerationService imageGenerationService;

    @InjectMocks
    private ImageGenerationController imageGenerationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateImage_withValidPrompt_shouldReturnImageUrl() {
        // Arrange
        String prompt = "a beautiful cake";
        String expectedUrl = "http://example.com/cake.png";
        Map<String, String> request = Map.of("prompt", prompt);

        when(imageGenerationService.generateImage(prompt)).thenReturn(expectedUrl);

        // Act
        ResponseEntity<?> response = imageGenerationController.generateImage(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals(expectedUrl, ((Map<?, ?>) response.getBody()).get("imageUrl"));
    }

    @Test
    void generateImage_withEmptyPrompt_shouldReturnBadRequest() {
        // Arrange
        Map<String, String> request = Map.of("prompt", " ");

        // Act
        ResponseEntity<?> response = imageGenerationController.generateImage(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        assertEquals("Prompt cannot be empty", ((Map<?, ?>) response.getBody()).get("error"));
    }

    @Test
    void generateCustomCakeImage_withValidRequest_shouldReturnGeneratedImage() {
        // Arrange
        IngredientDetailDTO ingredient = new IngredientDetailDTO("BIZCOCHO", "Vanilla", "Vanilla extract");
        CustomCakeImageRequestDTO requestDTO = new CustomCakeImageRequestDTO(
                "CUPCAKE",
                "Standard size",
                Collections.singletonList(ingredient),
                "For a small party"
        );

        GeneratedImageResponseDTO responseDTO = new GeneratedImageResponseDTO("Generated prompt", "http://example.com/cupcake.png");

        when(imageGenerationService.generateCustomCakeImage(any(CustomCakeImageRequestDTO.class))).thenReturn(responseDTO);

        // Act
        ResponseEntity<GeneratedImageResponseDTO> response = imageGenerationController.generateCustomCakeImage(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(responseDTO.getPrompt(), response.getBody().getPrompt());
        assertEquals(responseDTO.getImageUrl(), response.getBody().getImageUrl());
    }

    @Test
    void generateCustomCakeImage_withInvalidRequest_shouldReturnBadRequest() {
        // Arrange
        CustomCakeImageRequestDTO requestDTO = new CustomCakeImageRequestDTO(
                "",
                "Standard size",
                Collections.singletonList(new IngredientDetailDTO("BIZCOCHO", "Vanilla", "Vanilla extract")),
                "For a small party"
        );

        // Act
        ResponseEntity<GeneratedImageResponseDTO> response = imageGenerationController.generateCustomCakeImage(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }
}
