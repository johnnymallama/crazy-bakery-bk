package uan.edu.co.crazy_bakery.infrastructure.web.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.CiudadDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.DepartamentoDTO;
import uan.edu.co.crazy_bakery.application.services.GeografiaService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GeografiaControllerTest {

    @Mock
    private GeografiaService geografiaService;

    @InjectMocks
    private GeografiaController geografiaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDepartamentos_shouldReturnListOfDepartamentos() {
        // Arrange
        DepartamentoDTO departamento = new DepartamentoDTO();
        departamento.setId(1);
        departamento.setName("Amazonas");
        List<DepartamentoDTO> departamentos = Collections.singletonList(departamento);
        when(geografiaService.getDepartamentos()).thenReturn(departamentos);

        // Act
        List<DepartamentoDTO> result = geografiaController.getDepartamentos();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Amazonas", result.get(0).getName());
    }

    @Test
    void getCiudades_shouldReturnListOfCiudades() {
        // Arrange
        CiudadDTO ciudad = new CiudadDTO();
        ciudad.setId(1);
        ciudad.setName("Leticia");
        List<CiudadDTO> ciudades = Collections.singletonList(ciudad);
        when(geografiaService.getCiudades()).thenReturn(ciudades);

        // Act
        List<CiudadDTO> result = geografiaController.getCiudades();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Leticia", result.get(0).getName());
    }
}
