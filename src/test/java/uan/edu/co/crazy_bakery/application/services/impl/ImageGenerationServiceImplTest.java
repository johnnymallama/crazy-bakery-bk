package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.image.ImageGeneration;
import uan.edu.co.crazy_bakery.application.dto.requests.CustomCakeImageRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.IngredientDetailDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.GeneratedImageResponseDTO;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ImageGenerationServiceImplTest {

    @Mock
    private ImageModel imageModel;

    @InjectMocks
    private ImageGenerationServiceImpl imageGenerationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateImage() {
        // Arrange
        String prompt = "a simple cake";
        String expectedUrl = "http://example.com/image.png";
        Image image = new Image(expectedUrl, null);
        ImageGeneration imageGeneration = new ImageGeneration(image);
        ImageResponse imageResponse = new ImageResponse(List.of(imageGeneration));

        when(imageModel.call(any(ImagePrompt.class))).thenReturn(imageResponse);

        // Act
        String resultUrl = imageGenerationService.generateImage(prompt);

        // Assert
        assertNotNull(resultUrl);
        assertEquals(expectedUrl, resultUrl);
    }

    @Test
    void testGenerateCustomCakeImage() {
        // Arrange
        IngredientDetailDTO ingredient = new IngredientDetailDTO("BIZCOCHO", "Chocolate", "Chocolate");
        CustomCakeImageRequestDTO requestDTO = new CustomCakeImageRequestDTO(
                "TORTA",
                "Torta pequeña",
                Collections.singletonList(ingredient),
                "Birthday party"
        );

        String expectedUrl = "http://example.com/custom_cake.png";
        Image image = new Image(expectedUrl, null);
        ImageGeneration imageGeneration = new ImageGeneration(image);
        ImageResponse imageResponse = new ImageResponse(List.of(imageGeneration));

        when(imageModel.call(any(ImagePrompt.class))).thenReturn(imageResponse);

        // Act
        GeneratedImageResponseDTO responseDTO = imageGenerationService.generateCustomCakeImage(requestDTO);

        // Assert
        assertNotNull(responseDTO);
        assertEquals(expectedUrl, responseDTO.getImageUrl());
        assertNotNull(responseDTO.getPrompt());
        assertTrue(responseDTO.getPrompt().contains(requestDTO.getTipoReceta()));
        assertTrue(responseDTO.getPrompt().contains(requestDTO.getTamano()));
        assertTrue(responseDTO.getPrompt().contains(ingredient.getNombre()));
        assertTrue(responseDTO.getPrompt().contains(requestDTO.getDetalle()));
    }

    @Test
    void testGenerateCustomCakeImage_ingredientesNulos() {
        // Arrange
        CustomCakeImageRequestDTO requestDTO = new CustomCakeImageRequestDTO(
                "TORTA", "Torta pequeña", null, "Birthday party"
        );

        // Act & Assert
        assertThrows(NullPointerException.class, () ->
                imageGenerationService.generateCustomCakeImage(requestDTO)
        );
    }
}
