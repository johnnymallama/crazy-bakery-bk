package uan.edu.co.crazy_bakery.application.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper; // For converting the DTO to a JSON string

    public ImageGenerationServiceImpl(ImageModel imageModel, ObjectMapper objectMapper) {
        this.imageModel = imageModel;
        this.objectMapper = objectMapper;
    }

    @Override
    public String generateImage(String prompt) {
        ImagePrompt imagePrompt = new ImagePrompt(prompt);
        ImageResponse imageResponse = imageModel.call(imagePrompt);
        return resolveImageContent(imageResponse);
    }

    @Override
    public GeneratedImageResponseDTO generateCustomCakeImage(CustomCakeImageRequestDTO requestDTO) {
        String jsonPlaceholder = "";
        try {
            jsonPlaceholder = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestDTO);
        } catch (JsonProcessingException e) {
            // Handle the exception, maybe log it and return an error response
            throw new RuntimeException("Error converting request to JSON", e);
        }

        String promptTemplate = "Hola quiero que te comportes como un experto en repostería para mi compañía, para esta oportunidad necesito que me generes una imagen de una %s con el tamaño %s, que deben contener los siguientes ingredientes: %s. Adicional a esto, el cliente quiere este producto para la siguiente ocasión o evento: '%s'. Ten presente siempre que con la ocasión o evento, proponer una decoración no fuera de lo común, adicional a esto crea imagen lo mas abstractas posibles a la realidad. ---JSON PLACEHOLDER--- %s ---FIN JSON PLACEHOLDER---";

        String ingredientsString = requestDTO.getIngredientes().stream()
                .map(IngredientDetailDTO::getNombre)
                .collect(Collectors.joining(", "));

        String finalPrompt = String.format(promptTemplate,
                requestDTO.getTipoReceta(),
                requestDTO.getTamano(),
                ingredientsString,
                requestDTO.getDetalle(),
                jsonPlaceholder
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
