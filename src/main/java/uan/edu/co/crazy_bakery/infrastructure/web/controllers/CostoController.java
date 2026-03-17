
package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import uan.edu.co.crazy_bakery.application.dto.requests.CalcularCostoRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.CostoPedidoResponseDTO;
import uan.edu.co.crazy_bakery.application.services.CostoService;

@Tag(name = "Costo", description = "Cálculo de costos de pedidos")
@RestController
@RequestMapping("/costo")
@AllArgsConstructor
public class CostoController {

    private final CostoService costoService;

    @Operation(summary = "Calcular costo de un pedido", description = "Calcula el costo total de un pedido a partir de los ingredientes, tamaño y porcentajes configurados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Costo calculado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping("/calcular")
    public ResponseEntity<CostoPedidoResponseDTO> calcularCosto(@Valid @RequestBody CalcularCostoRequestDTO requestDTO) {
        CostoPedidoResponseDTO responseDTO = costoService.calcularCostoPedido(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
