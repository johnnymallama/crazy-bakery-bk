
package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

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

@RestController
@RequestMapping("/costo")
@AllArgsConstructor
public class CostoController {

    private final CostoService costoService;

    @PostMapping("/calcular")
    public ResponseEntity<CostoPedidoResponseDTO> calcularCosto(@Valid @RequestBody CalcularCostoRequestDTO requestDTO) {
        CostoPedidoResponseDTO responseDTO = costoService.calcularCostoPedido(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
