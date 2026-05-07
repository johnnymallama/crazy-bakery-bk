package uan.edu.co.crazy_bakery.application.services.impl;

import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.requests.CustomCakeImageRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.IngredientDetailDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.GeneratedImageResponseDTO;
import uan.edu.co.crazy_bakery.application.services.ImageGenerationService;
import uan.edu.co.crazy_bakery.application.services.storage.StorageService;

import java.io.IOException;
import java.util.Base64;
import java.util.stream.Collectors;

@Service
public class ImageGenerationServiceImpl implements ImageGenerationService {

    private final ImageModel imageModel;
    private final StorageService storageService;

    public ImageGenerationServiceImpl(ImageModel imageModel, StorageService storageService) {
        this.imageModel = imageModel;
        this.storageService = storageService;
    }

    @Override
    public String generateImage(String prompt) {
        ImagePrompt imagePrompt = new ImagePrompt(prompt);
        ImageResponse imageResponse = imageModel.call(imagePrompt);
        return uploadToTemp(imageResponse);
    }

    @Override
    public GeneratedImageResponseDTO generateCustomCakeImage(CustomCakeImageRequestDTO requestDTO) {
        String promptTemplate = "Fotografía profesional de pastelería de una %s de tamaño %s, hecha con los siguientes ingredientes: %s. La torta está decorada para la ocasión: '%s'. La decoración debe ser elegante, realista y fabricable por un pastelero profesional — sin elementos fantásticos ni imposibles. Fondo de un solo color neutro y liso (como blanco hueso, gris claro o beige suave) que resalte la torta. Iluminación de estudio, alta resolución, estilo editorial de revista de repostería.";

        String ingredientsString = requestDTO.getIngredientes().stream()
                .map(IngredientDetailDTO::getNombre)
                .collect(Collectors.joining(", "));

        String finalPrompt = String.format(promptTemplate,
                requestDTO.getTipoReceta(),
                requestDTO.getTamano(),
                ingredientsString,
                requestDTO.getDetalle()
        );

        ImagePrompt imagePrompt = new ImagePrompt(finalPrompt);
        ImageResponse imageResponse = imageModel.call(imagePrompt);
        String firebaseUrl = uploadToTemp(imageResponse);

        return new GeneratedImageResponseDTO(finalPrompt, firebaseUrl);
    }

    private String uploadToTemp(ImageResponse imageResponse) {
        Image image = imageResponse.getResult().getOutput();
        byte[] imageBytes = Base64.getDecoder().decode(image.getB64Json());
        String fileName = "temp/imagen-" + System.currentTimeMillis() + ".jpg";
        try {
            return storageService.uploadBytes(imageBytes, fileName, "image/jpeg");
        } catch (IOException e) {
            throw new RuntimeException("Error al subir imagen temporal a Firebase Storage", e);
        }
    }
}
