package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearRecetaDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.RecetaDTO;
import uan.edu.co.crazy_bakery.application.services.RecetaService;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RecetaController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class RecetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecetaService recetaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUltimasImagenes_shouldReturnListOfUrls() throws Exception {
        // Arrange
        List<String> imageUrls = Arrays.asList(
            "https://firebasestorage.googleapis.com/v0/b/crazy-bakery.appspot.com/o/receta-1698114435222.jpg?alt=media",
            "https://firebasestorage.googleapis.com/v0/b/crazy-bakery.appspot.com/o/receta-1698114468611.jpg?alt=media"
        );
        when(recetaService.getUltimasImagenes()).thenReturn(imageUrls);

        // Act & Assert
        mockMvc.perform(get("/receta/ultimas-imagenes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value(imageUrls.get(0)))
                .andExpect(jsonPath("$[1]").value(imageUrls.get(1)));
    }

    @Test
    void crearReceta_withValidRequest_shouldReturnCreated() throws Exception {
        // Arrange
        CrearRecetaDTO crearRecetaDTO = new CrearRecetaDTO();
        crearRecetaDTO.setTortaId(1L);
        crearRecetaDTO.setCantidad(1);
        crearRecetaDTO.setTipoReceta(TipoReceta.TORTA);
        crearRecetaDTO.setPrompt("A test prompt");
        crearRecetaDTO.setImagenUrl("http://example.com/image.png");

        RecetaDTO recetaDTO = new RecetaDTO();
        recetaDTO.setId(1L);
        recetaDTO.setPrompt("A test prompt");

        when(recetaService.crearReceta(any(CrearRecetaDTO.class))).thenReturn(recetaDTO);

        // Act & Assert
        mockMvc.perform(post("/receta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(crearRecetaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.prompt").value("A test prompt"));
    }

    @Test
    void obtenerRecetaPorId_whenRecetaExists_shouldReturnReceta() throws Exception {
        // Arrange
        long recetaId = 1L;
        RecetaDTO recetaDTO = new RecetaDTO();
        recetaDTO.setId(recetaId);

        when(recetaService.obtenerRecetaPorId(recetaId)).thenReturn(recetaDTO);

        // Act & Assert
        mockMvc.perform(get("/receta/{id}", recetaId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(recetaId));
    }

    @Test
    void obtenerRecetaPorId_whenRecetaNotFound_shouldReturnNotFound() throws Exception {
        // Arrange
        long recetaId = 2L;
        when(recetaService.obtenerRecetaPorId(anyLong())).thenThrow(new RuntimeException("Receta no encontrada"));

        // Act & Assert
        mockMvc.perform(get("/receta/{id}", recetaId))
                .andExpect(status().isNotFound());
    }
}
