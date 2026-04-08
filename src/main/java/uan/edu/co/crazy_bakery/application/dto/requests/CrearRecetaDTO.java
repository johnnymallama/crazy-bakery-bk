package uan.edu.co.crazy_bakery.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

@Schema(description = "Datos para crear una nueva receta de torta con imagen generada por IA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearRecetaDTO {

    @Schema(description = "Tipo de receta", example = "TORTA")
    @NotNull(message = "El tipo de receta no puede ser nulo")
    private TipoReceta tipoReceta;

    @Schema(description = "ID de la torta base (combinación de bizcocho, relleno y cubertura)", example = "3")
    @NotNull(message = "El id de la torta no puede ser nulo")
    private Long tortaId;

    @Schema(description = "Cantidad de unidades a preparar (mínimo 1)", example = "1")
    @Min(value = 1, message = "La cantidad debe ser como mínimo 1")
    private int cantidad;

    @Schema(description = "Prompt descriptivo utilizado para generar la imagen con DALL-E 3", example = "Torta de chocolate con fresas frescas y crema chantilly")
    @NotBlank(message = "El prompt no puede estar vacío")
    private String prompt;

    @Schema(description = "URL pública de la imagen generada en Firebase Storage", example = "https://storage.googleapis.com/crazy-bakery/imagen.png")
    @NotBlank(message = "La URL de la imagen no puede estar vacía")
    private String imagenUrl;

}
