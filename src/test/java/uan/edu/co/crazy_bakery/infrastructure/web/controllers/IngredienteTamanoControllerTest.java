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
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.application.services.IngredienteTamanoService;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.infrastructure.web.security.FirebaseTokenFilter;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = IngredienteTamanoController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FirebaseTokenFilter.class)
)
class IngredienteTamanoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngredienteTamanoService ingredienteTamanoService;

    @Autowired
    private ObjectMapper objectMapper;

    private IngredienteTamanoDTO ingredienteTamanoDTO;
    private CrearIngredienteTamanoDTO crearIngredienteTamanoDTO;

    @BeforeEach
    void setUp() {
        ingredienteTamanoDTO = new IngredienteTamanoDTO();
        ingredienteTamanoDTO.setId(1L);
        ingredienteTamanoDTO.setTamanoId(1L);

        crearIngredienteTamanoDTO = new CrearIngredienteTamanoDTO();
        crearIngredienteTamanoDTO.setTamanoId(1L);
        crearIngredienteTamanoDTO.setTipoIngrediente(TipoIngrediente.BIZCOCHO);
        crearIngredienteTamanoDTO.setGramos(100.0f);
    }

    @Test
    void consultarPorTamano_Success() throws Exception {
        when(ingredienteTamanoService.consultarPorTamano(1L)).thenReturn(Collections.singletonList(ingredienteTamanoDTO));

        mockMvc.perform(get("/ingrediente-tamano/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].tamanoId").value(1L));
    }

    @Test
    void crearRelacion_Success() throws Exception {
        when(ingredienteTamanoService.crearRelacion(any(CrearIngredienteTamanoDTO.class))).thenReturn(ingredienteTamanoDTO);

        mockMvc.perform(post("/ingrediente-tamano")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearIngredienteTamanoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tamanoId").value(1L));
    }

    @Test
    void inactivarRelacion_Success() throws Exception {
        when(ingredienteTamanoService.inactivarRelacion(1L)).thenReturn(true);

        mockMvc.perform(delete("/ingrediente-tamano/1"))
                .andExpect(status().isOk());
    }

    @Test
    void inactivarRelacion_NotFound() throws Exception {
        when(ingredienteTamanoService.inactivarRelacion(1L)).thenReturn(false);

        mockMvc.perform(delete("/ingrediente-tamano/1"))
                .andExpect(status().isNotFound());
    }
}
