package uan.edu.co.crazy_bakery.application.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearRecetaDTO {

    @NotNull(message = "El tipo de receta no puede ser nulo")
    private TipoReceta tipoReceta;

    @NotNull(message = "El id de la torta no puede ser nulo")
    private Long tortaId;

    @Min(value = 1, message = "La cantidad debe ser como mínimo 1")
    private int cantidad;

}
