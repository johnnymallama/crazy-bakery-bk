
package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uan.edu.co.crazy_bakery.application.dto.requests.CalcularCostoRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.CostoPedidoResponseDTO;
import uan.edu.co.crazy_bakery.application.services.CostoService;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CostoControllerTest {

    @Mock
    private CostoService costoService;

    @InjectMocks
    private CostoController costoController;

    private CalcularCostoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestDTO = new CalcularCostoRequestDTO();
        requestDTO.setTipoReceta(TipoReceta.TORTA);
        requestDTO.setTamanoId(1L);
        requestDTO.setIngredientesIds(Arrays.asList(1L, 2L, 3L));
        requestDTO.setCantidad(2);
    }

    @Test
    void testCalcularCosto() {
        // Arrange
        CostoPedidoResponseDTO responseDTO = new CostoPedidoResponseDTO(579600);
        when(costoService.calcularCostoPedido(any(CalcularCostoRequestDTO.class))).thenReturn(responseDTO);

        // Act
        ResponseEntity<CostoPedidoResponseDTO> result = costoController.calcularCosto(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(579600, result.getBody().getValorTotalPedido());
    }
}
