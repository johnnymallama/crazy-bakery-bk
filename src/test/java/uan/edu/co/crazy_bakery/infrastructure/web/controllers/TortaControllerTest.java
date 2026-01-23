package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.TamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.torta.CrearTortaDTO;
import uan.edu.co.crazy_bakery.application.dto.torta.TortaDTO;
import uan.edu.co.crazy_bakery.application.services.TortaService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TortaControllerTest {

    @Mock
    private TortaService tortaService;

    @InjectMocks
    private TortaController tortaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearTorta() {
        // Arrange
        CrearTortaDTO crearTortaDTO = new CrearTortaDTO();
        crearTortaDTO.setBizcochoId(1L);
        crearTortaDTO.setRellenoId(2L);
        crearTortaDTO.setCuberturaId(3L);
        crearTortaDTO.setTamanoId(4L);

        TortaDTO tortaDTOReturned = new TortaDTO();
        tortaDTOReturned.setId(1L);
        tortaDTOReturned.setBizcocho(new IngredienteDTO());
        tortaDTOReturned.setRelleno(new IngredienteDTO());
        tortaDTOReturned.setCubertura(new IngredienteDTO());
        tortaDTOReturned.setTamano(new TamanoDTO());
        tortaDTOReturned.getBizcocho().setId(1L);
        tortaDTOReturned.getRelleno().setId(2L);
        tortaDTOReturned.getCubertura().setId(3L);
        tortaDTOReturned.getTamano().setId(4L);


        when(tortaService.crearTorta(any(CrearTortaDTO.class))).thenReturn(tortaDTOReturned);

        // Act
        ResponseEntity<TortaDTO> result = tortaController.crearTorta(crearTortaDTO);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1L, result.getBody().getId());
        assertEquals(1L, result.getBody().getBizcocho().getId());
        assertEquals(2L, result.getBody().getRelleno().getId());
    }

    @Test
    void testObtenerTodasLasTortas() {
        // Arrange
        TortaDTO tortaDTO = new TortaDTO();
        tortaDTO.setId(1L);
        tortaDTO.setBizcocho(new IngredienteDTO());
        tortaDTO.getBizcocho().setId(1L);

        List<TortaDTO> tortaDTOList = Collections.singletonList(tortaDTO);

        when(tortaService.obtenerTodasLasTortas()).thenReturn(tortaDTOList);

        // Act
        ResponseEntity<List<TortaDTO>> result = tortaController.obtenerTodasLasTortas();

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size());
        assertEquals(tortaDTO, result.getBody().get(0));
    }
}
