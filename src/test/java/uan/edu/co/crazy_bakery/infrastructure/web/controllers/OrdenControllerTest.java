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
import uan.edu.co.crazy_bakery.application.dto.requests.AgregarNotaOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.AgregarRecetaOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CambiarEstadoOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.OrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.UsuarioDTO;
import uan.edu.co.crazy_bakery.application.services.OrdenService;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;
import uan.edu.co.crazy_bakery.infrastructure.web.security.FirebaseTokenFilter;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = OrdenController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FirebaseTokenFilter.class)
)
class OrdenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdenService ordenService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrdenDTO ordenDTO;

    @BeforeEach
    void setUp() {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId("user123");
        usuarioDTO.setNombre("Test User");

        ordenDTO = OrdenDTO.builder()
                .id(1L)
                .usuario(usuarioDTO)
                .build();
    }

    @Test
    void createOrden_Success() throws Exception {
        CrearOrdenDTO crearOrdenDTO = new CrearOrdenDTO();
        when(ordenService.createOrden(any(CrearOrdenDTO.class))).thenReturn(ordenDTO);

        mockMvc.perform(post("/orden")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearOrdenDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.usuario.id").value("user123"));
    }

    @Test
    void getAllOrdenes_Success() throws Exception {
        when(ordenService.getAllOrdenes()).thenReturn(Collections.singletonList(ordenDTO));

        mockMvc.perform(get("/orden"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].usuario.id").value("user123"));
    }

    @Test
    void getOrdenById_Success() throws Exception {
        when(ordenService.getOrdenById(1L)).thenReturn(ordenDTO);

        mockMvc.perform(get("/orden/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getOrdenesByUsuario_Success() throws Exception {
        when(ordenService.getOrdenesByUsuario("user123")).thenReturn(Collections.singletonList(ordenDTO));

        mockMvc.perform(get("/orden/usuario/user123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].usuario.id").value("user123"));
    }

    @Test
    void getOrdenesByEstado_Success() throws Exception {
        when(ordenService.getOrdenesByEstado(EstadoOrden.CREADO)).thenReturn(Collections.singletonList(ordenDTO));

        mockMvc.perform(get("/orden/estado/CREADO"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getOrdenesByFecha_Success() throws Exception {
        when(ordenService.getOrdenesByFecha(any(), any())).thenReturn(Collections.singletonList(ordenDTO));

        mockMvc.perform(get("/orden/fecha")
                        .param("fechaInicio", "2024-01-01")
                        .param("fechaFin", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void cambiarEstadoOrden_Success() throws Exception {
        CambiarEstadoOrdenDTO cambiarEstadoOrdenDTO = new CambiarEstadoOrdenDTO();
        cambiarEstadoOrdenDTO.setEstado(EstadoOrden.CONFIRMADO);

        when(ordenService.cambiarEstadoOrden(eq(1L), any(EstadoOrden.class))).thenReturn(ordenDTO);

        mockMvc.perform(patch("/orden/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambiarEstadoOrdenDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void agregarNotaOrden_Success() throws Exception {
        AgregarNotaOrdenDTO agregarNotaOrdenDTO = new AgregarNotaOrdenDTO();
        agregarNotaOrdenDTO.setNota("Sin gluten");
        agregarNotaOrdenDTO.setUsuarioId("user123");

        when(ordenService.agregarNotaOrden(eq(1L), any(AgregarNotaOrdenDTO.class))).thenReturn(ordenDTO);

        mockMvc.perform(patch("/orden/1/nota")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(agregarNotaOrdenDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void agregarRecetaOrden_Success() throws Exception {
        AgregarRecetaOrdenDTO agregarRecetaOrdenDTO = new AgregarRecetaOrdenDTO();
        agregarRecetaOrdenDTO.setRecetaId(2L);

        when(ordenService.agregarRecetaOrden(eq(1L), eq(2L))).thenReturn(ordenDTO);

        mockMvc.perform(patch("/orden/1/receta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(agregarRecetaOrdenDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
