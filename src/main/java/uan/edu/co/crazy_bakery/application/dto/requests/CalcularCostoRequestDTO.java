
package uan.edu.co.crazy_bakery.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

import java.util.List;

@Schema(description = "Parámetros para calcular el costo total de un pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CalcularCostoRequestDTO {

    @Schema(description = "Tipo de receta a calcular", example = "TORTA")
    @NotNull(message = "El tipo de receta no puede ser nulo")
    private TipoReceta tipoReceta;

    @Schema(description = "ID del tamaño de torta seleccionado (mínimo 1)", example = "2")
    @Min(value = 1, message = "El ID del tamaño debe ser mayor a 0")
    private Long tamanoId;

    @Schema(description = "Lista de IDs de ingredientes (mínimo 3: bizcocho, relleno y cubertura)", example = "[1, 3, 5]")
    @NotNull(message = "La lista de IDs de ingredientes no puede ser nula")
    @Size(min = 3, message = "La lista de ingredientes debe contener al menos 3 valores")
    private List<Long> ingredientesIds;

    @Schema(description = "Cantidad de unidades a producir (mínimo 1)", example = "2")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private int cantidad;
}
