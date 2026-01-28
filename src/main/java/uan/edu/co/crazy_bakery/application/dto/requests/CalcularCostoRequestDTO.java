
package uan.edu.co.crazy_bakery.application.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalcularCostoRequestDTO {

    @NotNull(message = "El tipo de receta no puede ser nulo")
    private TipoReceta tipoReceta;

    @Min(value = 1, message = "El ID del tamaño debe ser mayor a 0")
    private Long tamanoId;

    @NotNull(message = "La lista de IDs de ingredientes no puede ser nula")
    @Size(min = 3, message = "La lista de ingredientes debe contener al menos 3 valores")
    private List<Long> ingredientesIds;

    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private int cantidad;
}
