
package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uan.edu.co.crazy_bakery.application.dto.requests.CalcularCostoRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.CostoPedidoResponseDTO;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;
import uan.edu.co.crazy_bakery.domain.model.IngredienteCosto;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteCostoRepository;
import uan.edu.co.crazy_bakery.infrastructure.web.config.CostoProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CostoServiceImplTest {

    @Mock
    private IngredienteCostoRepository ingredienteCostoRepository;

    @Mock
    private CostoProperties costoProperties;

    @InjectMocks
    private CostoServiceImpl costoService;

    private CalcularCostoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new CalcularCostoRequestDTO();
        requestDTO.setTipoReceta(TipoReceta.TORTA);
        requestDTO.setTamanoId(1L);
        requestDTO.setIngredientesIds(Arrays.asList(1L, 2L, 3L));
        requestDTO.setCantidad(2);

        CostoProperties.Labor labor = new CostoProperties.Labor();
        labor.setValue(5000);
        CostoProperties.Operating operating = new CostoProperties.Operating();
        operating.setValue(3000);
        CostoProperties.Benefit benefit = new CostoProperties.Benefit();
        benefit.setPercentage(20);

        when(costoProperties.getLabor()).thenReturn(labor);
        when(costoProperties.getOperating()).thenReturn(operating);
        when(costoProperties.getBenefit()).thenReturn(benefit);
    }

    @Test
    void calcularCostoPedido_Success() {
        List<IngredienteCosto> ingredientesCostos = Arrays.asList(
                new IngredienteCosto(TipoReceta.TORTA, 1L, "Pequeña", 30.0f, null, 1L, "Harina", 1000),
                new IngredienteCosto(TipoReceta.TORTA, 1L, "Pequeña", 30.0f, null, 2L, "Azucar", 500),
                new IngredienteCosto(TipoReceta.TORTA, 1L, "Pequeña", 30.0f, null, 3L, "Huevos", 1500)
        );

        when(ingredienteCostoRepository.findByTipoRecetaAndTamanoIdAndIngredienteIdIn(
                requestDTO.getTipoReceta(),
                requestDTO.getTamanoId(),
                requestDTO.getIngredientesIds()))
                .thenReturn(ingredientesCostos);

        CostoPedidoResponseDTO response = costoService.calcularCostoPedido(requestDTO);

        // 1. valorTorta = 1000 + 500 + 1500 = 3000
        // 2.1. costoManoObraTotal = 5000 * 30.0 = 150000
        // 2.2. costoOperativoTotal = 3000 * 30.0 = 90000
        // 2.3. valorReceta = 2 * (150000 + 90000) = 480000
        // 2.4. costoTotalProducto = 480000 + 3000 = 483000
        // 2.5. gananciaTotal = (483000 * 20) / 100 = 96600
        // 2.6. valorTotalPedido = 483000 + 96600 = 579600

        assertEquals(579600, response.getValorTotalPedido(), 0.001);
    }

    @Test
    void calcularCostoPedido_NoIngredientsFound() {
        when(ingredienteCostoRepository.findByTipoRecetaAndTamanoIdAndIngredienteIdIn(
                requestDTO.getTipoReceta(),
                requestDTO.getTamanoId(),
                requestDTO.getIngredientesIds()))
                .thenReturn(Collections.emptyList());

        CostoPedidoResponseDTO response = costoService.calcularCostoPedido(requestDTO);

        // 1. valorTorta = 0
        // tamano_tiempo = 0.0
        // 2.1. costoManoObraTotal = 5000 * 0.0 = 0
        // 2.2. costoOperativoTotal = 3000 * 0.0 = 0
        // 2.3. valorReceta = 2 * (0 + 0) = 0
        // 2.4. costoTotalProducto = 0 + 0 = 0
        // 2.5. gananciaTotal = (0 * 20) / 100 = 0
        // 2.6. valorTotalPedido = 0 + 0 = 0

        assertEquals(0, response.getValorTotalPedido(), 0.001);
    }
}
