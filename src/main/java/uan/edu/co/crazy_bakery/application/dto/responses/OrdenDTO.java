package uan.edu.co.crazy_bakery.application.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;

import java.util.Date;
import java.util.List;

@Schema(description = "Representación completa de una orden de pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenDTO {

    @Schema(description = "ID único de la orden", example = "10")
    private Long id;

    @Schema(description = "Fecha y hora de creación de la orden", example = "2024-03-15T10:30:00")
    private Date fecha;

    @Schema(description = "Usuario que realizó el pedido")
    private UsuarioDTO usuario;

    @Schema(description = "Lista de recetas (tortas configuradas) incluidas en la orden")
    private List<RecetaDTO> recetas;

    @Schema(description = "Lista de notas o instrucciones especiales de la orden")
    private List<NotaDTO> notas;

    @Schema(description = "Estado actual de la orden (PENDIENTE, EN_PROCESO, ENTREGADO, CANCELADO)", example = "PENDIENTE")
    private EstadoOrden estado;

    @Schema(description = "Valor total de la orden en pesos colombianos", example = "120000.0")
    private float valorTotal;

    @Schema(description = "Ganancia estimada de la orden en pesos colombianos", example = "40000.0")
    private float ganancia;
}
