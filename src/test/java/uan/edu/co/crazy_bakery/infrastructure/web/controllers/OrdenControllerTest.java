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
import uan.edu.co.crazy_bakery.application.dto.responses.UsuarioDTO;
import uan.edu.co.crazy_bakery.application.services.OrdenService;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class OrdenControllerTest {

    @Mock
    private OrdenService ordenService;

    @InjectMocks
    private OrdenController ordenController;

    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId("user123");
        usuarioDTO.setNombre("Test User");
    }

    private OrdenDTO createOrdenDTO(Long id, UsuarioDTO usuario) {
        return OrdenDTO.builder()
                .id(id)
                .usuario(usuario)
                .build();
    }

    @Test
    void testCreateOrden() {
        CrearOrdenDTO crearOrdenDTO = new CrearOrdenDTO();
        OrdenDTO ordenDTO = createOrdenDTO(1L, usuarioDTO);

        when(ordenService.createOrden(any(CrearOrdenDTO.class))).thenReturn(ordenDTO);

        ResponseEntity<OrdenDTO> response = ordenController.createOrden(crearOrdenDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getUsuario());
        assertEquals("user123", response.getBody().getUsuario().getId());
    }

    @Test
    void testGetAllOrdenes() {
        List<OrdenDTO> ordenes = Collections.singletonList(createOrdenDTO(1L, usuarioDTO));
        when(ordenService.getAllOrdenes()).thenReturn(ordenes);

        ResponseEntity<List<OrdenDTO>> response = ordenController.getAllOrdenes();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertNotNull(response.getBody().get(0).getUsuario());
        assertEquals("user123", response.getBody().get(0).getUsuario().getId());
    }

    @Test
    void testGetOrdenById() {
        Long id = 1L;
        OrdenDTO ordenDTO = createOrdenDTO(id, usuarioDTO);
        when(ordenService.getOrdenById(id)).thenReturn(ordenDTO);

        ResponseEntity<OrdenDTO> response = ordenController.getOrdenById(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
        assertNotNull(response.getBody().getUsuario());
        assertEquals("user123", response.getBody().getUsuario().getId());
        assertEquals("Test User", response.getBody().getUsuario().getNombre());
    }

    @Test
    void testGetOrdenesByUsuario() {
        String usuarioId = "user123";
        List<OrdenDTO> ordenes = Collections.singletonList(createOrdenDTO(1L, usuarioDTO));
        when(ordenService.getOrdenesByUsuario(usuarioId)).thenReturn(ordenes);

        ResponseEntity<List<OrdenDTO>> response = ordenController.getOrdenesByUsuario(usuarioId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(usuarioId, Objects.requireNonNull(response.getBody()).get(0).getUsuario().getId());
    }

    @Test
    void testGetOrdenesByEstado() {
        EstadoOrden estado = EstadoOrden.CREADO;
        List<OrdenDTO> ordenes = Collections.singletonList(createOrdenDTO(1L, usuarioDTO));
        when(ordenService.getOrdenesByEstado(estado)).thenReturn(ordenes);

        ResponseEntity<List<OrdenDTO>> response = ordenController.getOrdenesByEstado(estado);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().get(0).getUsuario());
    }

    @Test
    void testGetOrdenesByFecha() {
        Date fechaInicio = new Date();
        Date fechaFin = new Date();
        List<OrdenDTO> ordenes = Collections.singletonList(createOrdenDTO(1L, usuarioDTO));
        when(ordenService.getOrdenesByFecha(fechaInicio, fechaFin)).thenReturn(ordenes);

        ResponseEntity<List<OrdenDTO>> response = ordenController.getOrdenesByFecha(fechaInicio, fechaFin);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().get(0).getUsuario());
    }

    @Test
    void testCambiarEstadoOrden() {
        Long ordenId = 1L;
        CambiarEstadoOrdenDTO cambiarEstadoOrdenDTO = new CambiarEstadoOrdenDTO();
        cambiarEstadoOrdenDTO.setEstado(EstadoOrden.CONFIRMADO);
        OrdenDTO ordenDTO = createOrdenDTO(ordenId, usuarioDTO);

        when(ordenService.cambiarEstadoOrden(eq(ordenId), any(EstadoOrden.class))).thenReturn(ordenDTO);

        ResponseEntity<OrdenDTO> response = ordenController.cambiarEstadoOrden(ordenId, cambiarEstadoOrdenDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getUsuario());
    }

    @Test
    void testAgregarNotaOrden() {
        Long ordenId = 1L;
        AgregarNotaOrdenDTO agregarNotaOrdenDTO = new AgregarNotaOrdenDTO();
        agregarNotaOrdenDTO.setNota("Test nota");
        OrdenDTO ordenDTO = createOrdenDTO(ordenId, usuarioDTO);

        // Corrección: usar any(AgregarNotaOrdenDTO.class) en lugar de any(String.class)
        when(ordenService.agregarNotaOrden(eq(ordenId), any(AgregarNotaOrdenDTO.class))).thenReturn(ordenDTO);

        ResponseEntity<OrdenDTO> response = ordenController.agregarNotaOrden(ordenId, agregarNotaOrdenDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getUsuario());
    }

    @Test
    void testAgregarRecetaOrden() {
        Long ordenId = 1L;
        AgregarRecetaOrdenDTO agregarRecetaOrdenDTO = new AgregarRecetaOrdenDTO();
        agregarRecetaOrdenDTO.setRecetaId(1L);
        OrdenDTO ordenDTO = createOrdenDTO(ordenId, usuarioDTO);

        when(ordenService.agregarRecetaOrden(eq(ordenId), any(Long.class))).thenReturn(ordenDTO);

        ResponseEntity<OrdenDTO> response = ordenController.agregarRecetaOrden(ordenId, agregarRecetaOrdenDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getUsuario());
    }
}
