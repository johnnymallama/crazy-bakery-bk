
package uan.edu.co.crazy_bakery.application.services;

import uan.edu.co.crazy_bakery.application.dto.requests.CalcularCostoRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.CostoPedidoResponseDTO;

public interface CostoService {

    CostoPedidoResponseDTO calcularCostoPedido(CalcularCostoRequestDTO requestDTO);

}
