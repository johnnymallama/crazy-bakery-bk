package uan.edu.co.crazy_bakery.application.dto.torta;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Datos para crear una torta combinando ingredientes y tamaño")
@Data
public class CrearTortaDTO {

    @Schema(description = "ID del ingrediente de tipo BIZCOCHO seleccionado", example = "1")
    private Long bizcochoId;

    @Schema(description = "ID del ingrediente de tipo RELLENO seleccionado", example = "3")
    private Long rellenoId;

    @Schema(description = "ID del ingrediente de tipo CUBERTURA seleccionado", example = "5")
    private Long cuberturaId;

    @Schema(description = "ID del tamaño de torta seleccionado", example = "2")
    private Long tamanoId;

}
