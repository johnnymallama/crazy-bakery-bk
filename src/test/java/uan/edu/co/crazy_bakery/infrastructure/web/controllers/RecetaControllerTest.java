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
import uan.edu.co.crazy_bakery.application.dto.torta.TortaDTO;
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

    private RecetaDTO recetaDTO;
    private CrearRecetaDTO crearRecetaDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        crearRecetaDTO = new CrearRecetaDTO();
        crearRecetaDTO.setTortaId(1L);
        crearRecetaDTO.setCantidad(1);
        crearRecetaDTO.setTipoReceta(TipoReceta.TORTA);

        TortaDTO torta = new TortaDTO();
        torta.setId(1L);

        recetaDTO = new RecetaDTO();
        recetaDTO.setId(1L);
        recetaDTO.setTorta(torta);
        recetaDTO.setCantidad(1);
        recetaDTO.setCostoTotal(50000f);
        recetaDTO.setEstado(true);
        recetaDTO.setTipoReceta(TipoReceta.TORTA);
    }

    @Test
    void testCrearReceta() {
        when(recetaService.crearReceta(any(CrearRecetaDTO.class))).thenReturn(recetaDTO);

        ResponseEntity<RecetaDTO> response = recetaController.crearReceta(crearRecetaDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(recetaDTO, response.getBody());
    }

    @Test
    void testObtenerRecetaPorId_Found() {
        when(recetaService.obtenerRecetaPorId(1L)).thenReturn(recetaDTO);

        ResponseEntity<RecetaDTO> response = recetaController.obtenerRecetaPorId(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(recetaDTO.getId(), response.getBody().getId());
    }

    @Test
    void testObtenerRecetaPorId_NotFound() {
        long recetaId = 99L;
        when(recetaService.obtenerRecetaPorId(recetaId)).thenThrow(new RuntimeException("Receta no encontrada con id: " + recetaId));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recetaController.obtenerRecetaPorId(recetaId);
        });

        String expectedMessage = "Receta no encontrada con id: " + recetaId;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
