package uan.edu.co.crazy_bakery.application.dto.requests;

import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;

import jakarta.validation.constraints.NotNull;

@Data
public class CambiarEstadoOrdenDTO {

    @NotNull(message = "El estado no puede ser nulo")
    private EstadoOrden estado;
}
