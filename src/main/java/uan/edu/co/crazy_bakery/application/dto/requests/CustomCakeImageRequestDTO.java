package uan.edu.co.crazy_bakery.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Schema(description = "Parámetros para generar una imagen de torta personalizada con DALL-E 3")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomCakeImageRequestDTO {

    @Schema(description = "Tipo de receta que define el estilo visual de la torta", example = "TORTA")
    @NotBlank(message = "El tipo de receta no puede estar vacío")
    private String tipoReceta;

    @Schema(description = "Descripción del tamaño de la torta", example = "Grande")
    @NotBlank(message = "El tamaño no puede estar vacío")
    private String tamano;

    @Schema(description = "Lista de ingredientes con su tipo, nombre y composición para el prompt de IA")
    @NotEmpty(message = "La lista de ingredientes no puede estar vacía")
    @NotNull(message = "La lista de ingredientes no puede ser nula")
    private List<IngredientDetailDTO> ingredientes;

    @Schema(description = "Detalle adicional o personalización especial para el prompt", example = "Decoración con flores de azúcar y mensaje de cumpleaños")
    private String detalle;
}
