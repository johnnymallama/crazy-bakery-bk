package uan.edu.co.crazy_bakery.application.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AgregarRecetaOrdenDTO {

    @NotNull(message = "El ID de la receta no puede ser nulo")
    private Long recetaId;
}
