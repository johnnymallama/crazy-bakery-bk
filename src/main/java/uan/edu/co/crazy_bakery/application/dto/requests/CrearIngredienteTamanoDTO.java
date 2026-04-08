package uan.edu.co.crazy_bakery.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

@Schema(description = "Datos para asociar un tipo de ingrediente a un tamaño con su cantidad en gramos")
@Data
public class CrearIngredienteTamanoDTO {

    @Schema(description = "ID del tamaño al que se asocia el ingrediente", example = "1")
    private Long tamanoId;

    @Schema(description = "Tipo de ingrediente (BIZCOCHO, RELLENO, CUBERTURA)", example = "BIZCOCHO")
    private TipoIngrediente tipoIngrediente;

    @Schema(description = "Cantidad de gramos requerida para este tamaño", example = "500.0")
    private float gramos;
}
