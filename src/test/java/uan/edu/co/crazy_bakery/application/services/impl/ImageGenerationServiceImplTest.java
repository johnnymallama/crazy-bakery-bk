package uan.edu.co.crazy_bakery.application.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

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
    void testGenerateCustomCakeImage() throws JsonProcessingException {
        // Arrange
        IngredientDetailDTO ingredient = new IngredientDetailDTO("BIZCOCHO", "Chocolate", "Chocolate");
        CustomCakeImageRequestDTO requestDTO = new CustomCakeImageRequestDTO(
                "TORTA",
                "Torta pequeña",
                Collections.singletonList(ingredient),
                "Birthday party"
        );

        String expectedJson = "{ \"tipo_receta\": \"TORTA\", ... }";
        String expectedUrl = "http://example.com/custom_cake.png";
        Image image = new Image(expectedUrl, null);
        ImageGeneration imageGeneration = new ImageGeneration(image);
        ImageResponse imageResponse = new ImageResponse(List.of(imageGeneration));

        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        when(objectWriter.writeValueAsString(any(CustomCakeImageRequestDTO.class))).thenReturn(expectedJson);
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
        assertTrue(responseDTO.getPrompt().contains(expectedJson));
    }

    @Test
    void testGenerateCustomCakeImage_JsonProcessingException() throws JsonProcessingException {
        // Arrange
        CustomCakeImageRequestDTO requestDTO = new CustomCakeImageRequestDTO();

        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        when(objectWriter.writeValueAsString(any(CustomCakeImageRequestDTO.class))).thenThrow(new JsonProcessingException("Test Exception"){});

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageGenerationService.generateCustomCakeImage(requestDTO);
        });

        assertEquals("Error converting request to JSON", exception.getMessage());
        assertTrue(exception.getCause() instanceof JsonProcessingException);
    }
}
