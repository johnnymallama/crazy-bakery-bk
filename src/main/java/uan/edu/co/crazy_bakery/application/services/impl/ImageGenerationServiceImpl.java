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

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ImageGenerationServiceImpl implements ImageGenerationService {

    private final ImageModel imageModel;

    public ImageGenerationServiceImpl(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @Override
    public String generateImage(String prompt) {
        ImagePrompt imagePrompt = new ImagePrompt(prompt);
        ImageResponse imageResponse = imageModel.call(imagePrompt);
        return resolveImageContent(imageResponse);
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

        String imageUrl = resolveImageContent(imageResponse);

        return new GeneratedImageResponseDTO(finalPrompt, imageUrl);
    }

    private String resolveImageContent(ImageResponse imageResponse) {
        Image image = imageResponse.getResult().getOutput();
        return Optional.ofNullable(image.getUrl())
                .orElseGet(image::getB64Json);
    }
}
