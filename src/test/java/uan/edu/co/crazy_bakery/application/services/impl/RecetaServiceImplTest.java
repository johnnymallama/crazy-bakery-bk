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
import uan.edu.co.crazy_bakery.infrastructure.repositories.RecetaRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TortaRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class RecetaServiceImplTest {

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private TortaRepository tortaRepository;

    @Mock
    private RecetaMapper recetaMapper;

    @InjectMocks
    private RecetaServiceImpl recetaService;

    private Receta receta;
    private RecetaDTO recetaDTO;
    private CrearRecetaDTO crearRecetaDTO;
    private Torta torta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        torta = new Torta();
        torta.setId(1L);
        torta.setValor(50000f);

        crearRecetaDTO = new CrearRecetaDTO();
        crearRecetaDTO.setTortaId(1L);
        crearRecetaDTO.setCantidad(1);

        receta = new Receta();
        receta.setId(1L);
        receta.setTorta(torta);
        receta.setCantidad(1);
        receta.setValor(50000f);
        receta.setEstado(true);

        TortaDTO torta = new TortaDTO();
        torta.setId(1L);

        recetaDTO = new RecetaDTO();
        recetaDTO.setId(1L);
        recetaDTO.setTorta(torta);
        recetaDTO.setCantidad(1);
        recetaDTO.setValor(50000f);
        recetaDTO.setEstado(true);
    }

    @Test
    void crearReceta_Success() {
        when(tortaRepository.findById(anyLong())).thenReturn(Optional.of(torta));
        when(recetaMapper.crearRecetaDTOToReceta(any(CrearRecetaDTO.class), any(Torta.class))).thenReturn(receta);
        when(recetaRepository.save(any(Receta.class))).thenReturn(receta);
        when(recetaMapper.recetaToRecetaDTO(any(Receta.class))).thenReturn(recetaDTO);

        RecetaDTO result = recetaService.crearReceta(crearRecetaDTO);

        assertNotNull(result);
        assertEquals(recetaDTO.getId(), result.getId());
        assertEquals(50000f, result.getValor());
        assertTrue(result.isEstado());
    }

    @Test
    void crearReceta_TortaNotFound() {
        when(tortaRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recetaService.crearReceta(crearRecetaDTO);
        });

        assertEquals("Torta no encontrada con id: " + crearRecetaDTO.getTortaId(), exception.getMessage());
    }

    @Test
    void obtenerRecetaPorId_Success() {
        when(recetaRepository.findById(anyLong())).thenReturn(Optional.of(receta));
        when(recetaMapper.recetaToRecetaDTO(any(Receta.class))).thenReturn(recetaDTO);

        RecetaDTO result = recetaService.obtenerRecetaPorId(1L);

        assertNotNull(result);
        assertEquals(recetaDTO.getId(), result.getId());
    }

    @Test
    void obtenerRecetaPorId_NotFound() {
        when(recetaRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            recetaService.obtenerRecetaPorId(99L);
        });

        assertEquals("Receta no encontrada con id: 99", exception.getMessage());
    }
}
