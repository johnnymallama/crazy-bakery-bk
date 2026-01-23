package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearRecetaDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.RecetaDTO;
import uan.edu.co.crazy_bakery.application.dto.torta.TortaDTO;
import uan.edu.co.crazy_bakery.application.mappers.RecetaMapper;
import uan.edu.co.crazy_bakery.domain.model.Receta;
import uan.edu.co.crazy_bakery.domain.model.Torta;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;
import uan.edu.co.crazy_bakery.infrastructure.repositories.RecetaRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TortaRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecetaServiceImplTest {

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private TortaRepository tortaRepository;

    @Mock
    private RecetaMapper recetaMapper;

    @InjectMocks
    private RecetaServiceImpl recetaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearReceta() {
        // Arrange
        long tortaId = 1L;
        CrearRecetaDTO crearRecetaDTO = new CrearRecetaDTO();
        crearRecetaDTO.setTortaId(tortaId);
        crearRecetaDTO.setCantidad(2);
        crearRecetaDTO.setTipoReceta(TipoReceta.TORTA);
        crearRecetaDTO.setPrompt("a delicious chocolate cake");
        crearRecetaDTO.setImagenUrl("http://example.com/cake.png");

        Torta torta = new Torta();
        torta.setId(tortaId);
        torta.setValor(50.0f);

        Receta receta = new Receta();
        receta.setTorta(torta);
        receta.setCantidad(crearRecetaDTO.getCantidad());
        receta.setTipoReceta(crearRecetaDTO.getTipoReceta());
        receta.setPrompt(crearRecetaDTO.getPrompt());
        receta.setImagenUrl(crearRecetaDTO.getImagenUrl());

        Receta recetaGuardada = new Receta();
        recetaGuardada.setId(1L);
        recetaGuardada.setTorta(torta);
        recetaGuardada.setCantidad(2);
        recetaGuardada.setCostoTotal(100.0f);
        recetaGuardada.setEstado(true);
        recetaGuardada.setPrompt("a delicious chocolate cake");
        recetaGuardada.setImagenUrl("http://example.com/cake.png");

        RecetaDTO expectedDto = new RecetaDTO();
        expectedDto.setId(1L);
        expectedDto.setTorta(new TortaDTO());
        expectedDto.setCantidad(2);
        expectedDto.setCostoTotal(100.0f);
        expectedDto.setEstado(true);
        expectedDto.setPrompt("a delicious chocolate cake");
        expectedDto.setImagenUrl("http://example.com/cake.png");

        when(tortaRepository.findById(tortaId)).thenReturn(Optional.of(torta));
        when(recetaMapper.crearRecetaDTOToReceta(crearRecetaDTO, torta)).thenReturn(receta);
        when(recetaRepository.save(any(Receta.class))).thenReturn(recetaGuardada);
        when(recetaMapper.recetaToRecetaDTO(recetaGuardada)).thenReturn(expectedDto);

        // Act
        RecetaDTO result = recetaService.crearReceta(crearRecetaDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getPrompt(), result.getPrompt());
        assertEquals(expectedDto.getImagenUrl(), result.getImagenUrl());
        assertEquals(100.0f, result.getCostoTotal());
        assertTrue(result.isEstado());

        verify(tortaRepository, times(1)).findById(tortaId);
        verify(recetaRepository, times(1)).save(any(Receta.class));
        verify(recetaMapper, times(1)).crearRecetaDTOToReceta(crearRecetaDTO, torta);
        verify(recetaMapper, times(1)).recetaToRecetaDTO(recetaGuardada);
    }

    @Test
    void testCrearReceta_TortaNotFound() {
        // Arrange
        long tortaId = 1L;
        CrearRecetaDTO crearRecetaDTO = new CrearRecetaDTO();
        crearRecetaDTO.setTortaId(tortaId);

        when(tortaRepository.findById(tortaId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recetaService.crearReceta(crearRecetaDTO);
        });

        assertEquals("Torta no encontrada con id: " + tortaId, exception.getMessage());
    }

    @Test
    void testObtenerRecetaPorId() {
        // Arrange
        long recetaId = 1L;
        Receta receta = new Receta();
        receta.setId(recetaId);
        receta.setPrompt("test prompt");
        receta.setImagenUrl("test url");

        RecetaDTO expectedDto = new RecetaDTO();
        expectedDto.setId(recetaId);
        expectedDto.setPrompt("test prompt");
        expectedDto.setImagenUrl("test url");

        when(recetaRepository.findById(recetaId)).thenReturn(Optional.of(receta));
        when(recetaMapper.recetaToRecetaDTO(receta)).thenReturn(expectedDto);

        // Act
        RecetaDTO result = recetaService.obtenerRecetaPorId(recetaId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getPrompt(), result.getPrompt());
        assertEquals(expectedDto.getImagenUrl(), result.getImagenUrl());

        verify(recetaRepository, times(1)).findById(recetaId);
        verify(recetaMapper, times(1)).recetaToRecetaDTO(receta);
    }

    @Test
    void testObtenerRecetaPorId_NotFound() {
        // Arrange
        long recetaId = 1L;
        when(recetaRepository.findById(recetaId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recetaService.obtenerRecetaPorId(recetaId);
        });

        assertEquals("Receta no encontrada con id: " + recetaId, exception.getMessage());
    }
}
