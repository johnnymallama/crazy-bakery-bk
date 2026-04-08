package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import uan.edu.co.crazy_bakery.application.dto.requests.CustomCakeImageRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.IngredientDetailDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.GeneratedImageResponseDTO;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageGenerationServiceImplTest {

    @Mock
    private ImageModel imageModel;

    @InjectMocks
    private ImageGenerationServiceImpl imageGenerationService;

    @Test
    void generateImage_ShouldReturnUrlDeImagen() {
        String prompt = "a simple cake";
        String expectedUrl = "http://example.com/image.png";
        ImageResponse imageResponse = new ImageResponse(List.of(new ImageGeneration(new Image(expectedUrl, null))));

        when(imageModel.call(any(ImagePrompt.class))).thenReturn(imageResponse);

        String result = imageGenerationService.generateImage(prompt);

        assertThat(result).isNotNull().isEqualTo(expectedUrl);
    }

    @Test
    void generateCustomCakeImage_ShouldReturnGeneratedImageResponseDTO() {
        IngredientDetailDTO ingredient = new IngredientDetailDTO("BIZCOCHO", "Chocolate", "Chocolate");
        CustomCakeImageRequestDTO requestDTO = new CustomCakeImageRequestDTO(
                "TORTA",
                "Torta pequeña",
                Collections.singletonList(ingredient),
                "Birthday party"
        );

        String expectedUrl = "http://example.com/custom_cake.png";
        ImageResponse imageResponse = new ImageResponse(List.of(new ImageGeneration(new Image(expectedUrl, null))));

        when(imageModel.call(any(ImagePrompt.class))).thenReturn(imageResponse);

        GeneratedImageResponseDTO result = imageGenerationService.generateCustomCakeImage(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo(expectedUrl);
        assertThat(result.getPrompt())
                .contains(requestDTO.getTipoReceta())
                .contains(requestDTO.getTamano())
                .contains(ingredient.getNombre())
                .contains(requestDTO.getDetalle());
    }

    @Test
    void generateCustomCakeImage_ShouldThrowExceptionCuandoIngredientesEsNull() {
        CustomCakeImageRequestDTO requestDTO = new CustomCakeImageRequestDTO(
                "TORTA", "Torta pequeña", null, "Birthday party"
        );

        assertThatThrownBy(() -> imageGenerationService.generateCustomCakeImage(requestDTO))
                .isInstanceOf(NullPointerException.class);
    }
}
