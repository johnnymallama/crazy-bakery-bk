package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.TamanoDTO;
import uan.edu.co.crazy_bakery.application.services.TamanoService;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TamanoControllerTest {

    @Mock
    private TamanoService tamanoService;

    @InjectMocks
    private TamanoController tamanoController;

    private TamanoDTO tamanoDTO;
    private CrearTamanoDTO crearTamanoDTO;
    private ActualizarTamanoDTO actualizarTamanoDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        tamanoDTO = new TamanoDTO();
        tamanoDTO.setId(1L);
        tamanoDTO.setNombre("Personal");

        crearTamanoDTO = new CrearTamanoDTO("Personal", 10, 15, 8, TipoReceta.TORTA);
        actualizarTamanoDTO = new ActualizarTamanoDTO(12, 18, 10);
    }

    @Test
    void testCrearTamano() {
        when(tamanoService.crearTamano(any(CrearTamanoDTO.class))).thenReturn(tamanoDTO);

        ResponseEntity<TamanoDTO> response = tamanoController.crearTamano(crearTamanoDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(tamanoDTO, response.getBody());
    }

    @Test
    void testObtenerTamanoPorId() {
        when(tamanoService.obtenerTamanoPorId(1L)).thenReturn(tamanoDTO);

        ResponseEntity<TamanoDTO> response = tamanoController.obtenerTamanoPorId(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tamanoDTO, response.getBody());
    }

    @Test
    void testObtenerTodosLosTamanos() {
        when(tamanoService.obtenerTodosLosTamanos()).thenReturn(Collections.singletonList(tamanoDTO));

        ResponseEntity<List<TamanoDTO>> response = tamanoController.obtenerTodosLosTamanos();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testActualizarTamano() {
        when(tamanoService.actualizarTamano(eq(1L), any(ActualizarTamanoDTO.class))).thenReturn(tamanoDTO);

        ResponseEntity<TamanoDTO> response = tamanoController.actualizarTamano(1L, actualizarTamanoDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tamanoDTO, response.getBody());
    }

    @Test
    void testEliminarTamano() {
        doNothing().when(tamanoService).eliminarTamano(1L);

        ResponseEntity<Void> response = tamanoController.eliminarTamano(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(tamanoService, times(1)).eliminarTamano(1L);
    }

    @Test
    void testObtenerTamanosPorTipoReceta() {
        when(tamanoService.obtenerTamanosPorTipoReceta(TipoReceta.TORTA)).thenReturn(Collections.singletonList(tamanoDTO));

        ResponseEntity<List<TamanoDTO>> response = tamanoController.obtenerTamanosPorTipoReceta(TipoReceta.TORTA);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
