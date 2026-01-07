package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.DepartamentoDTO;
import uan.edu.co.crazy_bakery.infrastructure.web.client.ColombiaApiClient;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GeografiaServiceImplTest {

    @Mock
    private ColombiaApiClient colombiaApiClient;

    @InjectMocks
    private GeografiaServiceImpl geografiaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDepartamentos_shouldCallApiClientAndReturnData() {
        // Arrange
        DepartamentoDTO departamento = new DepartamentoDTO();
        departamento.setId(1);
        departamento.setName("Test Department");
        List<DepartamentoDTO> expectedDepartamentos = Collections.singletonList(departamento);

        when(colombiaApiClient.getDepartamentos()).thenReturn(expectedDepartamentos);

        // Act
        List<DepartamentoDTO> actualDepartamentos = geografiaService.getDepartamentos();

        // Assert
        assertEquals(expectedDepartamentos, actualDepartamentos);
        verify(colombiaApiClient).getDepartamentos();
    }

    @Test
    void getCiudades_shouldCallApiClientAndReturnData() {
        // Arrange
        when(colombiaApiClient.getCiudades()).thenReturn(Collections.emptyList());

        // Act
        geografiaService.getCiudades();

        // Assert
        verify(colombiaApiClient).getCiudades();
    }
}
