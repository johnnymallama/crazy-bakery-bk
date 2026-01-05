package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngredienteServiceImplTest {

    @Mock
    private IngredienteRepository ingredienteRepository;

    @InjectMocks
    private IngredienteServiceImpl ingredienteService;

    private Ingrediente ingrediente;
    private CrearIngredienteDTO crearIngredienteDTO;

    @BeforeEach
    void setUp() {
        crearIngredienteDTO = new CrearIngredienteDTO();
        crearIngredienteDTO.setCodigo("M01");
        crearIngredienteDTO.setNombre("Harina de Trigo");
        crearIngredienteDTO.setComposicion("Trigo");
        crearIngredienteDTO.setTipoIngrediente(TipoIngrediente.MASA);
        crearIngredienteDTO.setValor(2500);

        ingrediente = new Ingrediente();
        ingrediente.setCodigo("M01");
        ingrediente.setNombre("Harina de Trigo");
        ingrediente.setComposicion("Trigo");
        ingrediente.setTipoIngrediente(TipoIngrediente.MASA);
        ingrediente.setValor(2500);
    }

    @Test
    void createIngrediente_Success() {
        // Arrange
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);

        // Act
        IngredienteDTO result = ingredienteService.createIngrediente(crearIngredienteDTO);

        // Assert
        assertNotNull(result);
        assertEquals("M01", result.getCodigo());
        assertEquals("Harina de Trigo", result.getNombre());
        verify(ingredienteRepository).save(any(Ingrediente.class));
    }

    @Test
    void getIngrediente_Found() {
        // Arrange
        when(ingredienteRepository.findById("M01")).thenReturn(Optional.of(ingrediente));

        // Act
        Optional<IngredienteDTO> result = ingredienteService.getIngrediente("M01");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("M01", result.get().getCodigo());
        verify(ingredienteRepository).findById("M01");
    }

    @Test
    void getIngrediente_NotFound() {
        // Arrange
        when(ingredienteRepository.findById("UNKNOWN")).thenReturn(Optional.empty());

        // Act
        Optional<IngredienteDTO> result = ingredienteService.getIngrediente("UNKNOWN");

        // Assert
        assertFalse(result.isPresent());
        verify(ingredienteRepository).findById("UNKNOWN");
    }

    @Test
    void getAllIngredientes_Success() {
        // Arrange
        when(ingredienteRepository.findAll()).thenReturn(List.of(ingrediente));

        // Act
        List<IngredienteDTO> result = ingredienteService.getAllIngredientes();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Harina de Trigo", result.get(0).getNombre());
        verify(ingredienteRepository).findAll();
    }

    @Test
    void getAllIngredientes_EmptyList() {
        // Arrange
        when(ingredienteRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<IngredienteDTO> result = ingredienteService.getAllIngredientes();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ingredienteRepository).findAll();
    }
}
