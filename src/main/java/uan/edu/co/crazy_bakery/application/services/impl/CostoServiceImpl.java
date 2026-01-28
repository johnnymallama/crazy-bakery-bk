
package uan.edu.co.crazy_bakery.application.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import uan.edu.co.crazy_bakery.application.dto.requests.CalcularCostoRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.CostoPedidoResponseDTO;
import uan.edu.co.crazy_bakery.application.services.CostoService;
import uan.edu.co.crazy_bakery.domain.model.IngredienteCosto;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteCostoRepository;
import uan.edu.co.crazy_bakery.infrastructure.web.config.CostoProperties;

@Service
@AllArgsConstructor
public class CostoServiceImpl implements CostoService {

    private final IngredienteCostoRepository ingredienteCostoRepository;
    private final CostoProperties costoProperties;

    @Override
    public CostoPedidoResponseDTO calcularCostoPedido(CalcularCostoRequestDTO requestDTO) {
        List<IngredienteCosto> ingredientesCostos = ingredienteCostoRepository.findByTipoRecetaAndTamanoIdAndIngredienteIdIn(
                requestDTO.getTipoReceta(),
                requestDTO.getTamanoId(),
                requestDTO.getIngredientesIds()
        );

        double valorTorta = ingredientesCostos.stream()
                .mapToDouble(IngredienteCosto::getIngredienteCostoTotal)
                .sum();

        double tamanoTiempo = ingredientesCostos.stream()
                .findFirst()
                .map(IngredienteCosto::getTamanoTiempo)
                .orElse(0.0f);

        double costoManoObraTotal = costoProperties.getLabor().getValue() * tamanoTiempo;
        double costoOperativoTotal = costoProperties.getOperating().getValue() * tamanoTiempo;

        double valorReceta = requestDTO.getCantidad() * (costoManoObraTotal + costoOperativoTotal);
        double costoTotalProducto = valorReceta + valorTorta;

        double gananciaTotal = (costoTotalProducto * costoProperties.getBenefit().getPercentage()) / 100;

        double valorTotalPedido = costoTotalProducto + gananciaTotal;

        return new CostoPedidoResponseDTO(valorTotalPedido);
    }
}
