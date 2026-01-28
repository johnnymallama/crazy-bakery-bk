package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.OrdenDTO;
import uan.edu.co.crazy_bakery.application.mappers.OrdenMapper;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;
import uan.edu.co.crazy_bakery.domain.model.Orden;
import uan.edu.co.crazy_bakery.domain.model.Receta;
import uan.edu.co.crazy_bakery.domain.model.Torta;
import uan.edu.co.crazy_bakery.domain.model.Usuario;
import uan.edu.co.crazy_bakery.infrastructure.repositories.OrdenRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.RecetaRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.UsuarioRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrdenServiceImplTest {

    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private OrdenMapper ordenMapper;

    private OrdenServiceImpl ordenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ordenService = new OrdenServiceImpl(ordenRepository, usuarioRepository, recetaRepository, ordenMapper, 10);
    }

    @Test
    void testCreateOrden() {
        CrearOrdenDTO crearOrdenDTO = new CrearOrdenDTO("1", List.of(1L), List.of("Nota 1"));
        Usuario usuario = new Usuario();
        Torta torta = new Torta();
        torta.setValor(10.0f);
        Receta receta = new Receta();
        receta.setTorta(torta);
        receta.setCostoManoObra(10.0f);
        receta.setCostoOperativo(20.0f);
        receta.setCantidad(1);
        Orden orden = new Orden();
        OrdenDTO ordenDTO = new OrdenDTO();
        ordenDTO.setValorTotal(44.0f);

        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuario));
        when(recetaRepository.findAllById(List.of(1L))).thenReturn(List.of(receta));
        when(ordenMapper.toEntity(crearOrdenDTO)).thenReturn(orden);
        when(ordenRepository.save(any(Orden.class))).thenReturn(orden);
        when(ordenMapper.toDto(orden)).thenReturn(ordenDTO);

        OrdenDTO result = ordenService.createOrden(crearOrdenDTO);

        assertNotNull(result);
        assertEquals(44.0f, result.getValorTotal());
    }

    @Test
    void testGetOrdenesByUsuario() {
        when(ordenRepository.findByUsuarioId("1")).thenReturn(List.of(new Orden()));
        when(ordenMapper.toDto(any(Orden.class))).thenReturn(new OrdenDTO());

        List<OrdenDTO> result = ordenService.getOrdenesByUsuario("1");

        assertFalse(result.isEmpty());
    }

    @Test
    void testGetOrdenesByEstado() {
        when(ordenRepository.findByEstado(EstadoOrden.CREADO)).thenReturn(List.of(new Orden()));
        when(ordenMapper.toDto(any(Orden.class))).thenReturn(new OrdenDTO());

        List<OrdenDTO> result = ordenService.getOrdenesByEstado(EstadoOrden.CREADO);

        assertFalse(result.isEmpty());
    }

    @Test
    void testGetOrdenesByFecha() {
        Date fechaInicio = new Date();
        Date fechaFin = new Date();

        when(ordenRepository.findByFechaGreaterThanEqualAndFechaLessThan(any(Date.class), any(Date.class))).thenReturn(List.of(new Orden()));
        when(ordenMapper.toDto(any(Orden.class))).thenReturn(new OrdenDTO());

        List<OrdenDTO> result = ordenService.getOrdenesByFecha(fechaInicio, fechaFin);

        assertFalse(result.isEmpty());
    }

    @Test
    void testCambiarEstadoOrden() {
        Long ordenId = 1L;
        EstadoOrden nuevoEstado = EstadoOrden.EN_PROCESO;
        Orden orden = new Orden();
        orden.setId(ordenId);
        orden.setEstado(EstadoOrden.CREADO);

        OrdenDTO ordenDTO = new OrdenDTO();
        ordenDTO.setId(ordenId);
        ordenDTO.setEstado(nuevoEstado);

        when(ordenRepository.findById(ordenId)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ordenMapper.toDto(any(Orden.class))).thenReturn(ordenDTO);

        OrdenDTO result = ordenService.cambiarEstadoOrden(ordenId, nuevoEstado);

        assertNotNull(result);
        assertEquals(nuevoEstado, result.getEstado());
        verify(ordenRepository).save(orden);
    }

    @Test
    void testAgregarNotaOrden() {
        Long ordenId = 1L;
        String nuevaNota = "Esta es una nota de prueba.";
        Orden orden = new Orden();
        orden.setId(ordenId);
        orden.setNotas(new ArrayList<>());

        OrdenDTO ordenDTO = new OrdenDTO();
        ordenDTO.setId(ordenId);
        ordenDTO.setNotas(List.of(nuevaNota));

        when(ordenRepository.findById(ordenId)).thenReturn(Optional.of(orden));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ordenMapper.toDto(any(Orden.class))).thenReturn(ordenDTO);

        OrdenDTO result = ordenService.agregarNotaOrden(ordenId, nuevaNota);

        assertNotNull(result);
        assertTrue(result.getNotas().contains(nuevaNota));
        verify(ordenRepository).save(orden);
    }

    @Test
    void testAgregarRecetaOrden() {
        Long ordenId = 1L;
        Long recetaId = 2L;
        Orden orden = new Orden();
        orden.setId(ordenId);
        orden.setRecetas(new ArrayList<>());
        orden.setValorTotal(50.0f);

        Torta torta = new Torta();
        torta.setValor(10.0f);

        Receta receta = new Receta();
        receta.setId(recetaId);
        receta.setCostoManoObra(10.0f);
        receta.setCostoOperativo(20.0f);
        receta.setCantidad(1);
        receta.setTorta(torta);

        OrdenDTO ordenDTO = new OrdenDTO();
        ordenDTO.setId(ordenId);
        ordenDTO.setValorTotal(44.0f);

        when(ordenRepository.findById(ordenId)).thenReturn(Optional.of(orden));
        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(receta));
        when(ordenRepository.save(any(Orden.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ordenMapper.toDto(any(Orden.class))).thenReturn(ordenDTO);

        OrdenDTO result = ordenService.agregarRecetaOrden(ordenId, recetaId);

        assertNotNull(result);
        assertEquals(44.0f, result.getValorTotal());
        verify(ordenRepository).save(orden);
    }
}
