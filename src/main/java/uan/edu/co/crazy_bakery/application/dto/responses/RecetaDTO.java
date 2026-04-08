package uan.edu.co.crazy_bakery.application.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uan.edu.co.crazy_bakery.application.dto.torta.TortaDTO;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

@Schema(description = "Representación de una receta: torta configurada con costos calculados e imagen generada por IA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecetaDTO {

    @Schema(description = "ID único de la receta", example = "5")
    private Long id;

    @Schema(description = "Tipo de receta", example = "TORTA")
    private TipoReceta tipoReceta;

    @Schema(description = "Torta base con sus ingredientes (bizcocho, relleno, cubertura) y tamaño")
    private TortaDTO torta;

    @Schema(description = "Cantidad de unidades de esta receta", example = "1")
    private int cantidad;

    @Schema(description = "Indica si la receta está activa", example = "true")
    private boolean estado;

    @Schema(description = "Prompt utilizado para generar la imagen con DALL-E 3", example = "Torta de chocolate con fresas frescas")
    private String prompt;

    @Schema(description = "URL pública de la imagen generada en Firebase Storage", example = "https://storage.googleapis.com/crazy-bakery/torta_005.png")
    private String imagenUrl;

}
