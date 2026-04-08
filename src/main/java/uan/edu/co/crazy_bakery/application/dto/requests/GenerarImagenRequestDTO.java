package uan.edu.co.crazy_bakery.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Datos para generar una imagen a partir de un prompt libre con DALL-E 3")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerarImagenRequestDTO {

    @Schema(description = "Descripción textual de la imagen a generar", example = "Torta de chocolate con fresas frescas y crema chantilly sobre fondo blanco")
    @NotBlank(message = "El prompt no puede estar vacío")
    private String prompt;
}
