package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uan.edu.co.crazy_bakery.application.dto.requests.CustomCakeImageRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.GenerarImagenRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.GeneratedImageResponseDTO;
import uan.edu.co.crazy_bakery.application.services.ImageGenerationService;


@Tag(name = "Generación de imágenes", description = "Generación de imágenes de tortas con IA (DALL-E 3)")
@RestController
@RequestMapping("/generate-image")
public class ImageGenerationController {

    private final ImageGenerationService imageGenerationService;

    public ImageGenerationController(ImageGenerationService imageGenerationService) {
        this.imageGenerationService = imageGenerationService;
    }

    @Operation(summary = "Generar imagen desde prompt", description = "Genera una imagen a partir de un prompt libre usando DALL-E 3 y la almacena en Firebase Storage")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen generada exitosamente, retorna la URL pública"),
        @ApiResponse(responseCode = "400", description = "El prompt está vacío o es inválido")
    })
    @PostMapping
    public ResponseEntity<GeneratedImageResponseDTO> generateImage(@Valid @RequestBody GenerarImagenRequestDTO request) {
        String imageUrl = imageGenerationService.generateImage(request.getPrompt());
        return ResponseEntity.ok(new GeneratedImageResponseDTO(request.getPrompt(), imageUrl));
    }

    @Operation(summary = "Generar imagen de torta personalizada", description = "Genera una imagen de torta personalizada según el tipo de receta y atributos proporcionados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen generada exitosamente"),
        @ApiResponse(responseCode = "400", description = "El tipo de receta es obligatorio")
    })
    @PostMapping("/custom-cake")
    public ResponseEntity<GeneratedImageResponseDTO> generateCustomCakeImage(@Valid @RequestBody CustomCakeImageRequestDTO requestDTO) {
        if (requestDTO.getTipoReceta() == null || requestDTO.getTipoReceta().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        GeneratedImageResponseDTO response = imageGenerationService.generateCustomCakeImage(requestDTO);
        return ResponseEntity.ok(response);
    }
}
