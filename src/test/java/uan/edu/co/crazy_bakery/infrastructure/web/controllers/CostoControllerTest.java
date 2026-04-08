package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uan.edu.co.crazy_bakery.application.dto.requests.CalcularCostoRequestDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.CostoPedidoResponseDTO;
import uan.edu.co.crazy_bakery.application.services.CostoService;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;
import uan.edu.co.crazy_bakery.infrastructure.web.security.FirebaseTokenFilter;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CostoController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FirebaseTokenFilter.class)
)
class CostoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CostoService costoService;

    @Autowired
    private ObjectMapper objectMapper;

    private CalcularCostoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new CalcularCostoRequestDTO();
        requestDTO.setTipoReceta(TipoReceta.TORTA);
        requestDTO.setTamanoId(1L);
        requestDTO.setIngredientesIds(Arrays.asList(1L, 2L, 3L));
        requestDTO.setCantidad(2);
    }

    @Test
    void calcularCosto_Success() throws Exception {
        CostoPedidoResponseDTO responseDTO = new CostoPedidoResponseDTO(579600);
        when(costoService.calcularCostoPedido(any(CalcularCostoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/costo/calcular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.valorTotalPedido").value(579600));
    }
}
