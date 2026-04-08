package uan.edu.co.crazy_bakery.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "Datos para crear una nueva orden de pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearOrdenDTO {

    @Schema(description = "UID del usuario de Firebase que realiza el pedido", example = "uid_firebase_abc123")
    private String usuarioId;

    @Schema(description = "Lista de IDs de recetas incluidas en la orden", example = "[1, 2]")
    private List<Long> recetaIds;

    @Schema(description = "Lista de notas o instrucciones especiales del cliente", example = "[\"Sin azúcar\", \"Entrega antes de las 6pm\"]")
    private List<String> notas;

}
