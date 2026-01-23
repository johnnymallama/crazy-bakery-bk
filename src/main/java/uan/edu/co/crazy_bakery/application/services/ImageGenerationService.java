package uan.edu.co.crazy_bakery.application.services;

import uan.edu.co.crazy_bakery.application.dto.requests.CustomCakeImageRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.GeneratedImageResponseDTO;

/**
 * Service for generating images.
 */
public interface ImageGenerationService {

    /**
     * Generates an image based on a simple text prompt.
     *
     * @param prompt the text prompt to generate the image from.
     * @return a String containing the URL or Base64-encoded data of the generated image.
     */
    String generateImage(String prompt);

    /**
     * Generates a custom cake image based on user specifications.
     *
     * @param requestDTO DTO containing the user's specifications.
     * @return a DTO containing the generated prompt and image URL.
     */
    GeneratedImageResponseDTO generateCustomCakeImage(CustomCakeImageRequestDTO requestDTO);

}
