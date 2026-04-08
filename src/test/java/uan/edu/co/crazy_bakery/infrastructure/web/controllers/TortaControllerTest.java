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
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.TamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.torta.CrearTortaDTO;
import uan.edu.co.crazy_bakery.application.dto.torta.TortaDTO;
import uan.edu.co.crazy_bakery.application.services.TortaService;
import uan.edu.co.crazy_bakery.infrastructure.web.security.FirebaseTokenFilter;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = TortaController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FirebaseTokenFilter.class)
)
class TortaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TortaService tortaService;

    @Autowired
    private ObjectMapper objectMapper;

    private TortaDTO tortaDTO;
    private CrearTortaDTO crearTortaDTO;

    @BeforeEach
    void setUp() {
        IngredienteDTO bizcocho = new IngredienteDTO();
        bizcocho.setId(1L);
        IngredienteDTO relleno = new IngredienteDTO();
        relleno.setId(2L);
        IngredienteDTO cubertura = new IngredienteDTO();
        cubertura.setId(3L);
        TamanoDTO tamano = new TamanoDTO();
        tamano.setId(4L);

        tortaDTO = new TortaDTO();
        tortaDTO.setId(1L);
        tortaDTO.setBizcocho(bizcocho);
        tortaDTO.setRelleno(relleno);
        tortaDTO.setCubertura(cubertura);
        tortaDTO.setTamano(tamano);

        crearTortaDTO = new CrearTortaDTO();
        crearTortaDTO.setBizcochoId(1L);
        crearTortaDTO.setRellenoId(2L);
        crearTortaDTO.setCuberturaId(3L);
        crearTortaDTO.setTamanoId(4L);
    }

    @Test
    void crearTorta_Success() throws Exception {
        when(tortaService.crearTorta(any(CrearTortaDTO.class))).thenReturn(tortaDTO);

        mockMvc.perform(post("/torta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearTortaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.bizcocho.id").value(1L))
                .andExpect(jsonPath("$.relleno.id").value(2L));
    }

    @Test
    void obtenerTodasLasTortas_Success() throws Exception {
        when(tortaService.obtenerTodasLasTortas()).thenReturn(Collections.singletonList(tortaDTO));

        mockMvc.perform(get("/torta"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].bizcocho.id").value(1L));
    }
}
