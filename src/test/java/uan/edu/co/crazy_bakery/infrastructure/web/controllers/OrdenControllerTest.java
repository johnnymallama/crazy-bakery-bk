package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uan.edu.co.crazy_bakery.application.dto.requests.AgregarNotaOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.AgregarRecetaOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CambiarEstadoOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.OrdenDTO;
import uan.edu.co.crazy_bakery.application.services.OrdenService;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class OrdenControllerTest {

    @Mock
    private OrdenService ordenService;

    @InjectMocks
    private OrdenController ordenController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrden() {
        CrearOrdenDTO crearOrdenDTO = new CrearOrdenDTO();
        OrdenDTO ordenDTO = new OrdenDTO();

        when(ordenService.createOrden(any(CrearOrdenDTO.class))).thenReturn(ordenDTO);

        ResponseEntity<OrdenDTO> response = ordenController.createOrden(crearOrdenDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(ordenDTO, response.getBody());
    }

    @Test
    void testGetAllOrdenes() {
        List<OrdenDTO> ordenes = Collections.singletonList(new OrdenDTO());
        when(ordenService.getAllOrdenes()).thenReturn(ordenes);

        ResponseEntity<List<OrdenDTO>> response = ordenController.getAllOrdenes();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ordenes, response.getBody());
    }

    @Test
    void testGetOrdenById() {
        Long id = 1L;
        OrdenDTO ordenDTO = new OrdenDTO();
        when(ordenService.getOrdenById(id)).thenReturn(ordenDTO);

        ResponseEntity<OrdenDTO> response = ordenController.getOrdenById(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ordenDTO, response.getBody());
    }

    @Test
    void testGetOrdenesByUsuario() {
        String usuarioId = "user1";
        List<OrdenDTO> ordenes = Collections.singletonList(new OrdenDTO());
        when(ordenService.getOrdenesByUsuario(usuarioId)).thenReturn(ordenes);

        ResponseEntity<List<OrdenDTO>> response = ordenController.getOrdenesByUsuario(usuarioId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ordenes, response.getBody());
    }

    @Test
    void testGetOrdenesByEstado() {
        EstadoOrden estado = EstadoOrden.CREADO;
        List<OrdenDTO> ordenes = Collections.singletonList(new OrdenDTO());
        when(ordenService.getOrdenesByEstado(estado)).thenReturn(ordenes);

        ResponseEntity<List<OrdenDTO>> response = ordenController.getOrdenesByEstado(estado);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ordenes, response.getBody());
    }

    @Test
    void testGetOrdenesByFecha() {
        Date fechaInicio = new Date();
        Date fechaFin = new Date();
        List<OrdenDTO> ordenes = Collections.singletonList(new OrdenDTO());
        when(ordenService.getOrdenesByFecha(fechaInicio, fechaFin)).thenReturn(ordenes);

        ResponseEntity<List<OrdenDTO>> response = ordenController.getOrdenesByFecha(fechaInicio, fechaFin);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ordenes, response.getBody());
    }

    @Test
    void testCambiarEstadoOrden() {
        Long ordenId = 1L;
        CambiarEstadoOrdenDTO cambiarEstadoOrdenDTO = new CambiarEstadoOrdenDTO();
        cambiarEstadoOrdenDTO.setEstado(EstadoOrden.CONFIRMADO);
        OrdenDTO ordenDTO = new OrdenDTO();

        when(ordenService.cambiarEstadoOrden(eq(ordenId), any(EstadoOrden.class))).thenReturn(ordenDTO);

        ResponseEntity<OrdenDTO> response = ordenController.cambiarEstadoOrden(ordenId, cambiarEstadoOrdenDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ordenDTO, response.getBody());
    }

    @Test
    void testAgregarNotaOrden() {
        Long ordenId = 1L;
        AgregarNotaOrdenDTO agregarNotaOrdenDTO = new AgregarNotaOrdenDTO();
        agregarNotaOrdenDTO.setNota("Test nota");
        OrdenDTO ordenDTO = new OrdenDTO();

        when(ordenService.agregarNotaOrden(eq(ordenId), any(String.class))).thenReturn(ordenDTO);

        ResponseEntity<OrdenDTO> response = ordenController.agregarNotaOrden(ordenId, agregarNotaOrdenDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ordenDTO, response.getBody());
    }

    @Test
    void testAgregarRecetaOrden() {
        Long ordenId = 1L;
        AgregarRecetaOrdenDTO agregarRecetaOrdenDTO = new AgregarRecetaOrdenDTO();
        agregarRecetaOrdenDTO.setRecetaId(1L);
        OrdenDTO ordenDTO = new OrdenDTO();

        when(ordenService.agregarRecetaOrden(eq(ordenId), any(Long.class))).thenReturn(ordenDTO);

        ResponseEntity<OrdenDTO> response = ordenController.agregarRecetaOrden(ordenId, agregarRecetaOrdenDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ordenDTO, response.getBody());
    }
}
