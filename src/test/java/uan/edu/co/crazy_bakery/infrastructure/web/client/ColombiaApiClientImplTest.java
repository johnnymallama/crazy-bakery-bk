package uan.edu.co.crazy_bakery.infrastructure.web.client;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.CiudadDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.DepartamentoDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ColombiaApiClientImplTest {

    @Autowired
    private ColombiaApiClient colombiaApiClient;

    @Test
    @Disabled("This test makes a real API call and should be run manually or in a specific integration test phase")
    void getDepartamentos_shouldReturnDataFromApi() {
        // Act
        List<DepartamentoDTO> departamentos = colombiaApiClient.getDepartamentos();

        // Assert
        assertNotNull(departamentos);
        assertFalse(departamentos.isEmpty());
    }

    @Test
    @Disabled("This test makes a real API call and should be run manually or in a specific integration test phase")
    void getCiudades_shouldReturnDataFromApi() {
        // Act
        List<CiudadDTO> ciudades = colombiaApiClient.getCiudades();

        // Assert
        assertNotNull(ciudades);
        assertFalse(ciudades.isEmpty());
    }
}
