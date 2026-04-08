package uan.edu.co.crazy_bakery.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Datos para vincular una receta a una orden existente")
@Data
public class AgregarRecetaOrdenDTO {

    @Schema(description = "ID de la receta a agregar en la orden", example = "5")
    @NotNull(message = "El ID de la receta no puede ser nulo")
    private Long recetaId;
}
