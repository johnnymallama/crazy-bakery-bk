package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearRecetaDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.RecetaDTO;
import uan.edu.co.crazy_bakery.application.services.RecetaService;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RecetaControllerTest {

    @Mock
    private RecetaService recetaService;

    @InjectMocks
    private RecetaController recetaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearReceta_withValidRequest_shouldReturnCreated() {
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
        recetaDTO.setImagenUrl("http://example.com/image.png");

        when(recetaService.crearReceta(any(CrearRecetaDTO.class))).thenReturn(recetaDTO);

        // Act
        ResponseEntity<RecetaDTO> response = recetaController.crearReceta(crearRecetaDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("A test prompt", response.getBody().getPrompt());
    }

    @Test
    void obtenerRecetaPorId_whenRecetaExists_shouldReturnReceta() {
        // Arrange
        long recetaId = 1L;
        RecetaDTO recetaDTO = new RecetaDTO();
        recetaDTO.setId(recetaId);
        recetaDTO.setPrompt("A test prompt for existing recipe");
        recetaDTO.setImagenUrl("http://example.com/existing.png");

        when(recetaService.obtenerRecetaPorId(recetaId)).thenReturn(recetaDTO);

        // Act
        ResponseEntity<RecetaDTO> response = recetaController.obtenerRecetaPorId(recetaId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(recetaId, response.getBody().getId());
    }

    @Test
    void obtenerRecetaPorId_whenRecetaNotFound_shouldReturnNotFound() {
        // Arrange
        long recetaId = 2L;
        when(recetaService.obtenerRecetaPorId(recetaId)).thenThrow(new RuntimeException("Receta no encontrada"));

        // Act
        ResponseEntity<RecetaDTO> response = recetaController.obtenerRecetaPorId(recetaId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
