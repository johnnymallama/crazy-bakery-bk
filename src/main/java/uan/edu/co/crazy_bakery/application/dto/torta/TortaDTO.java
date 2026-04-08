package uan.edu.co.crazy_bakery.application.dto.torta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.TamanoDTO;

@Schema(description = "Representación de una torta con sus componentes: bizcocho, relleno, cubertura y tamaño")
@Data
public class TortaDTO {

    @Schema(description = "ID único de la torta", example = "1")
    private Long id;

    @Schema(description = "Ingrediente de tipo BIZCOCHO seleccionado para la base de la torta")
    private IngredienteDTO bizcocho;

    @Schema(description = "Ingrediente de tipo RELLENO seleccionado para el interior de la torta")
    private IngredienteDTO relleno;

    @Schema(description = "Ingrediente de tipo CUBERTURA seleccionado para la decoración exterior")
    private IngredienteDTO cubertura;

    @Schema(description = "Tamaño de la torta con sus dimensiones y porciones")
    private TamanoDTO tamano;

    @Schema(description = "Valor calculado de la torta en pesos colombianos", example = "65000.0")
    private float valor;

    @Schema(description = "Indica si la torta está activa en el catálogo", example = "true")
    private boolean estado;

}
