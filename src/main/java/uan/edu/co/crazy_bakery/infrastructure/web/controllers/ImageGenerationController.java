package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uan.edu.co.crazy_bakery.application.dto.requests.CustomCakeImageRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.GeneratedImageResponseDTO;
import uan.edu.co.crazy_bakery.application.services.ImageGenerationService;

import java.util.Map;

@RestController
@RequestMapping("/generate-image")
public class ImageGenerationController {

    private final ImageGenerationService imageGenerationService;

    public ImageGenerationController(ImageGenerationService imageGenerationService) {
        this.imageGenerationService = imageGenerationService;
    }

    @PostMapping
    public ResponseEntity<?> generateImage(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        if (prompt == null || prompt.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Prompt cannot be empty"));
        }

        String imageUrl = imageGenerationService.generateImage(prompt);
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }

    @PostMapping("/custom-cake")
    public ResponseEntity<GeneratedImageResponseDTO> generateCustomCakeImage(@Valid @RequestBody CustomCakeImageRequestDTO requestDTO) {
        if (requestDTO.getTipoReceta() == null || requestDTO.getTipoReceta().trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        GeneratedImageResponseDTO response = imageGenerationService.generateCustomCakeImage(requestDTO);
        return ResponseEntity.ok(response);
    }
}
