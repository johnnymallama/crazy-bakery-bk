package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.services.IngredienteService;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = IngredienteController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class IngredienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngredienteService ingredienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private IngredienteDTO ingredienteDTO;
    private CrearIngredienteDTO crearIngredienteDTO;

    @BeforeEach
    void setUp() {
        ingredienteDTO = new IngredienteDTO();
        ingredienteDTO.setId(1L);
        ingredienteDTO.setNombre("Harina de Trigo");
        ingredienteDTO.setComposicion("Trigo");
        ingredienteDTO.setTipoIngrediente(TipoIngrediente.BIZCOCHO);
        ingredienteDTO.setValor(2500);

        crearIngredienteDTO = new CrearIngredienteDTO();
        crearIngredienteDTO.setNombre("Harina de Trigo");
        crearIngredienteDTO.setComposicion("Trigo");
        crearIngredienteDTO.setTipoIngrediente(TipoIngrediente.BIZCOCHO);
        crearIngredienteDTO.setValor(2500);
    }

    @Test
    void createIngrediente_Success() throws Exception {
        when(ingredienteService.createIngrediente(any(CrearIngredienteDTO.class))).thenReturn(ingredienteDTO);

        mockMvc.perform(post("/ingredientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearIngredienteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Harina de Trigo"));
    }

    @Test
    void getIngrediente_Found() throws Exception {
        when(ingredienteService.getIngrediente(1L)).thenReturn(Optional.of(ingredienteDTO));

        mockMvc.perform(get("/ingredientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Harina de Trigo"));
    }

    @Test
    void getIngrediente_NotFound() throws Exception {
        when(ingredienteService.getIngrediente(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/ingredientes/1"))
                .andExpect(status().isNotFound());
    }
}
