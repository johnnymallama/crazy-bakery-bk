package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

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
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.CiudadDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.DepartamentoDTO;
import uan.edu.co.crazy_bakery.application.services.GeografiaService;
import uan.edu.co.crazy_bakery.infrastructure.web.security.FirebaseTokenFilter;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = GeografiaController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = FirebaseTokenFilter.class)
)
class GeografiaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeografiaService geografiaService;

    private DepartamentoDTO departamentoDTO;
    private CiudadDTO ciudadDTO;

    @BeforeEach
    void setUp() {
        departamentoDTO = new DepartamentoDTO();
        departamentoDTO.setId(1);
        departamentoDTO.setName("Amazonas");

        ciudadDTO = new CiudadDTO();
        ciudadDTO.setId(1);
        ciudadDTO.setName("Leticia");
    }

    @Test
    void getDepartamentos_Success() throws Exception {
        when(geografiaService.getDepartamentos()).thenReturn(Collections.singletonList(departamentoDTO));

        mockMvc.perform(get("/geografia/departamentos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Amazonas"));
    }

    @Test
    void getCiudades_Success() throws Exception {
        when(geografiaService.getCiudades()).thenReturn(Collections.singletonList(ciudadDTO));

        mockMvc.perform(get("/geografia/ciudades"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Leticia"));
    }
}
