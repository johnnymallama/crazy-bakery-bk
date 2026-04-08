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
import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.TamanoDTO;
import uan.edu.co.crazy_bakery.application.services.TamanoService;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;
import uan.edu.co.crazy_bakery.infrastructure.web.security.FirebaseTokenFilter;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = TamanoController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FirebaseTokenFilter.class)
)
class TamanoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TamanoService tamanoService;

    @Autowired
    private ObjectMapper objectMapper;

    private TamanoDTO tamanoDTO;
    private CrearTamanoDTO crearTamanoDTO;
    private ActualizarTamanoDTO actualizarTamanoDTO;

    @BeforeEach
    void setUp() {
        tamanoDTO = new TamanoDTO();
        tamanoDTO.setId(1L);
        tamanoDTO.setNombre("Personal");

        crearTamanoDTO = new CrearTamanoDTO("Personal", 10, 15, 8, TipoReceta.TORTA, 1.0F);
        actualizarTamanoDTO = new ActualizarTamanoDTO(12, 18, 10, 1.0F);
    }

    @Test
    void crearTamano_Success() throws Exception {
        when(tamanoService.crearTamano(any(CrearTamanoDTO.class))).thenReturn(tamanoDTO);

        mockMvc.perform(post("/tamanos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearTamanoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Personal"));
    }

    @Test
    void obtenerTamanoPorId_Success() throws Exception {
        when(tamanoService.obtenerTamanoPorId(1L)).thenReturn(tamanoDTO);

        mockMvc.perform(get("/tamanos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Personal"));
    }

    @Test
    void obtenerTodosLosTamanos_Success() throws Exception {
        when(tamanoService.obtenerTodosLosTamanos()).thenReturn(Collections.singletonList(tamanoDTO));

        mockMvc.perform(get("/tamanos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Personal"));
    }

    @Test
    void actualizarTamano_Success() throws Exception {
        when(tamanoService.actualizarTamano(eq(1L), any(ActualizarTamanoDTO.class))).thenReturn(tamanoDTO);

        mockMvc.perform(put("/tamanos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizarTamanoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void eliminarTamano_Success() throws Exception {
        doNothing().when(tamanoService).eliminarTamano(1L);

        mockMvc.perform(delete("/tamanos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void obtenerTamanosPorTipoReceta_Success() throws Exception {
        when(tamanoService.obtenerTamanosPorTipoReceta(TipoReceta.TORTA)).thenReturn(Collections.singletonList(tamanoDTO));

        mockMvc.perform(get("/tamanos/tipo-receta/TORTA"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
