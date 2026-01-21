package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uan.edu.co.crazy_bakery.application.dto.torta.CrearTortaDTO;
import uan.edu.co.crazy_bakery.application.dto.torta.TortaDTO;
import uan.edu.co.crazy_bakery.application.mappers.TortaMapper;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;
import uan.edu.co.crazy_bakery.domain.model.IngredienteTamano;
import uan.edu.co.crazy_bakery.domain.model.Tamano;
import uan.edu.co.crazy_bakery.domain.model.Torta;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteTamanoRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TamanoRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TortaRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class TortaServiceImplTest {

    @Mock
    private TortaRepository tortaRepository;
    @Mock
    private IngredienteRepository ingredienteRepository;
    @Mock
    private TamanoRepository tamanoRepository;
    @Mock
    private IngredienteTamanoRepository ingredienteTamanoRepository;
    @Mock
    private TortaMapper tortaMapper;

    @InjectMocks
    private TortaServiceImpl tortaService;

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

        Ingrediente bizcocho = new Ingrediente();
        bizcocho.setId(1L);
        bizcocho.setCostoPorGramo(10.0f);

        Ingrediente relleno = new Ingrediente();
        relleno.setId(2L);
        relleno.setCostoPorGramo(5.0f);

        Ingrediente cubertura = new Ingrediente();
        cubertura.setId(3L);
        cubertura.setCostoPorGramo(8.0f);

        Tamano tamano = new Tamano();
        tamano.setId(4L);

        IngredienteTamano itBizcocho = new IngredienteTamano();
        itBizcocho.setGramos(100);
        IngredienteTamano itRelleno = new IngredienteTamano();
        itRelleno.setGramos(50);
        IngredienteTamano itCubertura = new IngredienteTamano();
        itCubertura.setGramos(80);

        Torta torta = new Torta();
        torta.setId(1L);

        TortaDTO tortaDTO = new TortaDTO();
        tortaDTO.setId(1L);

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(bizcocho));
        when(ingredienteRepository.findById(2L)).thenReturn(Optional.of(relleno));
        when(ingredienteRepository.findById(3L)).thenReturn(Optional.of(cubertura));
        when(tamanoRepository.findById(4L)).thenReturn(Optional.of(tamano));

        when(ingredienteTamanoRepository.findByTamanoAndTipoIngredienteAndEstado(tamano, TipoIngrediente.BIZCOCHO, true)).thenReturn(Optional.of(itBizcocho));
        when(ingredienteTamanoRepository.findByTamanoAndTipoIngredienteAndEstado(tamano, TipoIngrediente.RELLENO, true)).thenReturn(Optional.of(itRelleno));
        when(ingredienteTamanoRepository.findByTamanoAndTipoIngredienteAndEstado(tamano, TipoIngrediente.COBERTURA, true)).thenReturn(Optional.of(itCubertura));

        when(tortaRepository.save(any(Torta.class))).thenReturn(torta);
        when(tortaMapper.toDTO(torta)).thenReturn(tortaDTO);

        // Act
        TortaDTO result = tortaService.crearTorta(crearTortaDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testCrearTortaBizcochoNotFound() {
        // Arrange
        CrearTortaDTO crearTortaDTO = new CrearTortaDTO();
        crearTortaDTO.setBizcochoId(1L);

        when(ingredienteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> tortaService.crearTorta(crearTortaDTO));
        assertEquals("Bizcocho no encontrado", exception.getMessage());
    }

    @Test
    void testCrearTortaRellenoNotFound() {
        // Arrange
        CrearTortaDTO crearTortaDTO = new CrearTortaDTO();
        crearTortaDTO.setBizcochoId(1L);
        crearTortaDTO.setRellenoId(2L);

        Ingrediente bizcocho = new Ingrediente();
        bizcocho.setId(1L);

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(bizcocho));
        when(ingredienteRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> tortaService.crearTorta(crearTortaDTO));
        assertEquals("Relleno no encontrado", exception.getMessage());
    }

    @Test
    void testCrearTortaCuberturaNotFound() {
        // Arrange
        CrearTortaDTO crearTortaDTO = new CrearTortaDTO();
        crearTortaDTO.setBizcochoId(1L);
        crearTortaDTO.setRellenoId(2L);
        crearTortaDTO.setCuberturaId(3L);

        Ingrediente bizcocho = new Ingrediente();
        bizcocho.setId(1L);
        Ingrediente relleno = new Ingrediente();
        relleno.setId(2L);

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(bizcocho));
        when(ingredienteRepository.findById(2L)).thenReturn(Optional.of(relleno));
        when(ingredienteRepository.findById(3L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> tortaService.crearTorta(crearTortaDTO));
        assertEquals("Cubertura no encontrada", exception.getMessage());
    }

    @Test
    void testCrearTortaTamanoNotFound() {
        // Arrange
        CrearTortaDTO crearTortaDTO = new CrearTortaDTO();
        crearTortaDTO.setBizcochoId(1L);
        crearTortaDTO.setRellenoId(2L);
        crearTortaDTO.setCuberturaId(3L);
        crearTortaDTO.setTamanoId(4L);

        Ingrediente bizcocho = new Ingrediente();
        bizcocho.setId(1L);
        Ingrediente relleno = new Ingrediente();
        relleno.setId(2L);
        Ingrediente cubertura = new Ingrediente();
        cubertura.setId(3L);

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(bizcocho));
        when(ingredienteRepository.findById(2L)).thenReturn(Optional.of(relleno));
        when(ingredienteRepository.findById(3L)).thenReturn(Optional.of(cubertura));
        when(tamanoRepository.findById(4L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> tortaService.crearTorta(crearTortaDTO));
        assertEquals("Tamaño no encontrado", exception.getMessage());
    }

    @Test
    void testCalcularCostoComponenteNotFound() {
        // Arrange
        CrearTortaDTO crearTortaDTO = new CrearTortaDTO();
        crearTortaDTO.setBizcochoId(1L);
        crearTortaDTO.setRellenoId(2L);
        crearTortaDTO.setCuberturaId(3L);
        crearTortaDTO.setTamanoId(4L);

        Ingrediente bizcocho = new Ingrediente();
        bizcocho.setId(1L);
        bizcocho.setCostoPorGramo(10.0f);

        Tamano tamano = new Tamano();
        tamano.setId(4L);

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(bizcocho));
        when(ingredienteRepository.findById(2L)).thenReturn(Optional.of(new Ingrediente()));
        when(ingredienteRepository.findById(3L)).thenReturn(Optional.of(new Ingrediente()));
        when(tamanoRepository.findById(4L)).thenReturn(Optional.of(tamano));

        when(ingredienteTamanoRepository.findByTamanoAndTipoIngredienteAndEstado(tamano, TipoIngrediente.BIZCOCHO, true)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> tortaService.crearTorta(crearTortaDTO));
        assertEquals("No se encontró la relación ingrediente-tamaño para BIZCOCHO", exception.getMessage());
    }

    @Test
    void testObtenerTodasLasTortas() {
        // Arrange
        Torta torta = new Torta();
        torta.setId(1L);
        List<Torta> tortaList = Collections.singletonList(torta);

        TortaDTO tortaDTO = new TortaDTO();
        tortaDTO.setId(1L);

        when(tortaRepository.findAll()).thenReturn(tortaList);
        when(tortaMapper.toDTO(torta)).thenReturn(tortaDTO);

        // Act
        List<TortaDTO> result = tortaService.obtenerTodasLasTortas();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}
