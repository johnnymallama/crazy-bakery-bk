package uan.edu.co.crazy_bakery.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Detalle de un ingrediente para incluir en el prompt de generación de imagen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDetailDTO {

    @Schema(description = "Tipo de ingrediente (BIZCOCHO, RELLENO, CUBERTURA)", example = "RELLENO")
    @NotBlank(message = "El tipo de ingrediente no puede estar vacío")
    private String tipoIngrediente;

    @Schema(description = "Nombre del ingrediente", example = "Mermelada de fresa")
    @NotBlank(message = "El nombre del ingrediente no puede estar vacío")
    private String nombre;

    @Schema(description = "Descripción de la composición o sabor del ingrediente", example = "Mermelada artesanal de fresa con trozos de fruta")
    @NotBlank(message = "La composición del ingrediente no puede estar vacía")
    private String composicion;
}
