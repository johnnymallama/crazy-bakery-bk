package uan.edu.co.crazy_bakery.application.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Resultado de la generación de imagen con DALL-E 3")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedImageResponseDTO {

    @Schema(description = "Prompt utilizado para generar la imagen", example = "Torta de vainilla con fresas y crema chantilly")
    private String prompt;

    @Schema(description = "URL pública de la imagen almacenada en Firebase Storage", example = "https://storage.googleapis.com/crazy-bakery/torta_001.png")
    private String imageUrl;

}
