package uan.edu.co.crazy_bakery.application.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

@Schema(description = "Relación entre un tipo de ingrediente y un tamaño de torta con su cantidad en gramos")
@Data
public class IngredienteTamanoDTO {

    @Schema(description = "ID único de la relación", example = "1")
    private Long id;

    @Schema(description = "ID del tamaño de torta asociado", example = "2")
    private Long tamanoId;

    @Schema(description = "Nombre descriptivo del tamaño", example = "Grande")
    private String tamanoNombre;

    @Schema(description = "Tipo de ingrediente (BIZCOCHO, RELLENO, CUBERTURA)", example = "BIZCOCHO")
    private TipoIngrediente tipoIngrediente;

    @Schema(description = "Cantidad de gramos requerida para este tamaño", example = "500.0")
    private float gramos;
}
