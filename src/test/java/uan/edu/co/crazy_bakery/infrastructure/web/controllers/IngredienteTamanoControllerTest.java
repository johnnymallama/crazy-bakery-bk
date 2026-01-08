package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.application.services.IngredienteTamanoService;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class IngredienteTamanoControllerTest {

    @Mock
    private IngredienteTamanoService ingredienteTamanoService;

    @InjectMocks
    private IngredienteTamanoController ingredienteTamanoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsultarPorTamano() {
        // Arrange
        Long tamanoId = 1L;
        IngredienteTamanoDTO dto = new IngredienteTamanoDTO();
        dto.setId(1L);
        dto.setTamanoId(tamanoId);
        List<IngredienteTamanoDTO> dtoList = Collections.singletonList(dto);
        when(ingredienteTamanoService.consultarPorTamano(tamanoId)).thenReturn(dtoList);

        // Act
        ResponseEntity<List<IngredienteTamanoDTO>> response = ingredienteTamanoController.consultarPorTamano(tamanoId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testCrearRelacion() {
        // Arrange
        CrearIngredienteTamanoDTO crearDto = new CrearIngredienteTamanoDTO();
        crearDto.setTamanoId(1L);
        crearDto.setTipoIngrediente(TipoIngrediente.BIZCOCHO);
        crearDto.setGramos(100.0f);

        IngredienteTamanoDTO resultDto = new IngredienteTamanoDTO();
        resultDto.setId(1L);
        resultDto.setTamanoId(crearDto.getTamanoId());

        when(ingredienteTamanoService.crearRelacion(any(CrearIngredienteTamanoDTO.class))).thenReturn(resultDto);

        // Act
        ResponseEntity<IngredienteTamanoDTO> response = ingredienteTamanoController.crearRelacion(crearDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testInactivarRelacion_whenFound() {
        // Arrange
        Long id = 1L;
        when(ingredienteTamanoService.inactivarRelacion(id)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = ingredienteTamanoController.inactivarRelacion(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInactivarRelacion_whenNotFound() {
        // Arrange
        Long id = 1L;
        when(ingredienteTamanoService.inactivarRelacion(id)).thenReturn(false);

        // Act
        ResponseEntity<Void> response = ingredienteTamanoController.inactivarRelacion(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
