package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import uan.edu.co.crazy_bakery.application.services.storage.StorageService;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageGenerationServiceImplTest {

    @Mock
    private ImageModel imageModel;

    @Mock
    private StorageService storageService;

    private ImageGenerationServiceImpl imageGenerationService;

    private static final String FAKE_B64 = Base64.getEncoder().encodeToString("fake-image-bytes".getBytes());
    private static final String FIREBASE_TEMP_URL = "https://firebasestorage.googleapis.com/v0/b/bucket/o/temp%2Fimagen-123.jpg?alt=media";

    @BeforeEach
    void setUp() {
        imageGenerationService = new ImageGenerationServiceImpl(imageModel, storageService);
    }

    @Test
    void generateImage_ShouldReturnUrlDeFirebaseTemp() throws IOException {
        ImageResponse imageResponse = new ImageResponse(List.of(new ImageGeneration(new Image(null, FAKE_B64))));

        when(imageModel.call(any(ImagePrompt.class))).thenReturn(imageResponse);
        when(storageService.uploadBytes(any(byte[].class), anyString(), eq("image/jpeg"))).thenReturn(FIREBASE_TEMP_URL);

        String result = imageGenerationService.generateImage("a simple cake");

        assertThat(result).isNotNull().isEqualTo(FIREBASE_TEMP_URL);
        verify(storageService, times(1)).uploadBytes(any(byte[].class), anyString(), eq("image/jpeg"));
    }

    @Test
    void generateImage_ShouldThrowRuntimeExceptionCuandoStorageFalla() throws IOException {
        ImageResponse imageResponse = new ImageResponse(List.of(new ImageGeneration(new Image(null, FAKE_B64))));

        when(imageModel.call(any(ImagePrompt.class))).thenReturn(imageResponse);
        when(storageService.uploadBytes(any(byte[].class), anyString(), anyString())).thenThrow(new IOException("Error de red"));

        assertThatThrownBy(() -> imageGenerationService.generateImage("a simple cake"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error al subir imagen temporal a Firebase Storage");
    }

    @Test
    void generateCustomCakeImage_ShouldReturnGeneratedImageResponseDTO() throws IOException {
        IngredientDetailDTO ingredient = new IngredientDetailDTO("BIZCOCHO", "Chocolate", "Chocolate");
        CustomCakeImageRequestDTO requestDTO = new CustomCakeImageRequestDTO(
                "TORTA",
                "Torta pequeña",
                Collections.singletonList(ingredient),
                "Birthday party"
        );

        ImageResponse imageResponse = new ImageResponse(List.of(new ImageGeneration(new Image(null, FAKE_B64))));

        when(imageModel.call(any(ImagePrompt.class))).thenReturn(imageResponse);
        when(storageService.uploadBytes(any(byte[].class), anyString(), eq("image/jpeg"))).thenReturn(FIREBASE_TEMP_URL);

        GeneratedImageResponseDTO result = imageGenerationService.generateCustomCakeImage(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo(FIREBASE_TEMP_URL);
        assertThat(result.getPrompt())
                .contains(requestDTO.getTipoReceta())
                .contains(requestDTO.getTamano())
                .contains(ingredient.getNombre())
                .contains(requestDTO.getDetalle());
        verify(storageService, times(1)).uploadBytes(any(byte[].class), anyString(), eq("image/jpeg"));
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
