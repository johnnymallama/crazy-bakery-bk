package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.application.mappers.IngredienteTamanoMapper;
import uan.edu.co.crazy_bakery.application.services.IngredienteTamanoServiceImpl;
import uan.edu.co.crazy_bakery.domain.model.IngredienteTamano;
import uan.edu.co.crazy_bakery.domain.model.Tamano;
import uan.edu.co.crazy_bakery.infrastructure.repositories.IngredienteTamanoRepository;
import uan.edu.co.crazy_bakery.infrastructure.repositories.TamanoRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class IngredienteTamanoServiceTest {

    @Mock
    private IngredienteTamanoRepository ingredienteTamanoRepository;

    @Mock
    private TamanoRepository tamanoRepository;

    @Mock
    private IngredienteTamanoMapper ingredienteTamanoMapper;

    @InjectMocks
    private IngredienteTamanoServiceImpl ingredienteTamanoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsultarPorTamano() {
        // Arrange
        Long tamanoId = 1L;
        IngredienteTamano ingredienteTamano = new IngredienteTamano();
        List<IngredienteTamano> list = Collections.singletonList(ingredienteTamano);
        when(ingredienteTamanoRepository.findByTamanoIdAndEstadoTrue(tamanoId)).thenReturn(list);

        IngredienteTamanoDTO dto = new IngredienteTamanoDTO();
        when(ingredienteTamanoMapper.ingredienteTamanoToIngredienteTamanoDTO(any(IngredienteTamano.class))).thenReturn(dto);

        // Act
        List<IngredienteTamanoDTO> result = ingredienteTamanoService.consultarPorTamano(tamanoId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testCrearRelacion() {
        // Arrange
        CrearIngredienteTamanoDTO crearDto = new CrearIngredienteTamanoDTO();
        crearDto.setTamanoId(1L);

        Tamano tamano = new Tamano();
        when(tamanoRepository.findByIdAndEstadoTrue(anyLong())).thenReturn(Optional.of(tamano));

        IngredienteTamano saved = new IngredienteTamano();
        when(ingredienteTamanoRepository.save(any(IngredienteTamano.class))).thenReturn(saved);

        IngredienteTamanoDTO dto = new IngredienteTamanoDTO();
        when(ingredienteTamanoMapper.ingredienteTamanoToIngredienteTamanoDTO(any(IngredienteTamano.class))).thenReturn(dto);

        // Act
        IngredienteTamanoDTO result = ingredienteTamanoService.crearRelacion(crearDto);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testInactivarRelacion_whenFound() {
        // Arrange
        Long id = 1L;
        IngredienteTamano ingredienteTamano = new IngredienteTamano();
        when(ingredienteTamanoRepository.findById(id)).thenReturn(Optional.of(ingredienteTamano));

        // Act
        boolean result = ingredienteTamanoService.inactivarRelacion(id);

        // Assert
        assertTrue(result);
    }

    @Test
    void testInactivarRelacion_whenNotFound() {
        // Arrange
        Long id = 1L;
        when(ingredienteTamanoRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        boolean result = ingredienteTamanoService.inactivarRelacion(id);

        // Assert
        assertFalse(result);
    }
}
