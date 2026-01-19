package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import uan.edu.co.crazy_bakery.application.services.TipoIngredienteService;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TipoIngredienteController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class TipoIngredienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TipoIngredienteService tipoIngredienteService;

    @Test
    void getAllTiposIngrediente_Success() throws Exception {
        List<TipoIngrediente> tipos = Arrays.asList(TipoIngrediente.values());
        when(tipoIngredienteService.getAllTiposIngrediente()).thenReturn(tipos);

        mockMvc.perform(get("/tipo-ingrediente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(TipoIngrediente.BIZCOCHO.toString()))
                .andExpect(jsonPath("$[1]").value(TipoIngrediente.COBERTURA.toString()));
    }
}
