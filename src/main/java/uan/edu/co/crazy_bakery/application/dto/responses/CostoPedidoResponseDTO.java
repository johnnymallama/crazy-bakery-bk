
package uan.edu.co.crazy_bakery.application.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Resultado del cálculo de costo de un pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CostoPedidoResponseDTO {

    @Schema(description = "Valor total estimado del pedido en pesos colombianos", example = "85000.0")
    private double valorTotalPedido;
}
