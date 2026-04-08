package uan.edu.co.crazy_bakery.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;

import jakarta.validation.constraints.NotNull;

@Schema(description = "Nuevo estado a asignar a la orden")
@Data
public class CambiarEstadoOrdenDTO {

    @Schema(description = "Estado destino de la orden (PENDIENTE, EN_PROCESO, ENTREGADO, CANCELADO)", example = "EN_PROCESO")
    @NotNull(message = "El estado no puede ser nulo")
    private EstadoOrden estado;
}
