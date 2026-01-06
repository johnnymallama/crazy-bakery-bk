package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uan.edu.co.crazy_bakery.application.dto.requests.ActualizarTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.TamanoDTO;
import uan.edu.co.crazy_bakery.application.mappers.TamanoMapper;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;
import uan.edu.co.crazy_bakery.domain.model.Tamano;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TamanoRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TamanoServiceImplTest {

    @Mock
    private TamanoRepository tamanoRepository;

    @Mock
    private TamanoMapper tamanoMapper;

    @InjectMocks
    private TamanoServiceImpl tamanoService;

    private Tamano tamano;
    private TamanoDTO tamanoDTO;
    private CrearTamanoDTO crearTamanoDTO;
    private ActualizarTamanoDTO actualizarTamanoDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        crearTamanoDTO = new CrearTamanoDTO("Personal", 10, 15, 8, TipoReceta.TORTA);

        actualizarTamanoDTO = new ActualizarTamanoDTO(12, 18, 10);

        tamano = new Tamano();
        tamano.setId(1L);
        tamano.setNombre("Personal");
        tamano.setEstado(true);

        tamanoDTO = new TamanoDTO();
        tamanoDTO.setId(1L);
        tamanoDTO.setNombre("Personal");
    }

    @Test
    void testCrearTamano() {
        when(tamanoMapper.crearTamanoDTOToTamano(any(CrearTamanoDTO.class))).thenReturn(tamano);
        when(tamanoRepository.save(any(Tamano.class))).thenReturn(tamano);
        when(tamanoMapper.tamanoToTamanoDTO(any(Tamano.class))).thenReturn(tamanoDTO);

        TamanoDTO result = tamanoService.crearTamano(crearTamanoDTO);

        assertNotNull(result);
        assertEquals(tamanoDTO.getId(), result.getId());
        verify(tamanoRepository, times(1)).save(any(Tamano.class));
    }

    @Test
    void testObtenerTamanoPorId() {
        when(tamanoRepository.findByIdAndEstadoTrue(1L)).thenReturn(Optional.of(tamano));
        when(tamanoMapper.tamanoToTamanoDTO(any(Tamano.class))).thenReturn(tamanoDTO);

        TamanoDTO result = tamanoService.obtenerTamanoPorId(1L);

        assertNotNull(result);
        assertEquals(tamanoDTO.getId(), result.getId());
    }

    @Test
    void testObtenerTamanoPorId_NotFound() {
        when(tamanoRepository.findByIdAndEstadoTrue(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> tamanoService.obtenerTamanoPorId(1L));
    }

    @Test
    void testObtenerTodosLosTamanos() {
        when(tamanoRepository.findAllByEstadoTrue()).thenReturn(Collections.singletonList(tamano));
        when(tamanoMapper.tamanosToTamanoDTOs(anyList())).thenReturn(Collections.singletonList(tamanoDTO));

        List<TamanoDTO> result = tamanoService.obtenerTodosLosTamanos();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testActualizarTamano() {
        when(tamanoRepository.findByIdAndEstadoTrue(1L)).thenReturn(Optional.of(tamano));
        when(tamanoRepository.save(any(Tamano.class))).thenReturn(tamano);
        when(tamanoMapper.tamanoToTamanoDTO(any(Tamano.class))).thenReturn(tamanoDTO);

        TamanoDTO result = tamanoService.actualizarTamano(1L, actualizarTamanoDTO);

        assertNotNull(result);
        assertEquals(tamanoDTO.getId(), result.getId());
        verify(tamanoRepository, times(1)).save(tamano);
    }

    @Test
    void testEliminarTamano() {
        when(tamanoRepository.findByIdAndEstadoTrue(1L)).thenReturn(Optional.of(tamano));
        
        tamanoService.eliminarTamano(1L);

        assertFalse(tamano.isEstado());
        verify(tamanoRepository, times(1)).save(tamano);
    }
}
