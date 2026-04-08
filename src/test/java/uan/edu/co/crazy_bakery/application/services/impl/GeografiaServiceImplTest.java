package uan.edu.co.crazy_bakery.application.services.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.CiudadDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.DepartamentoDTO;
import uan.edu.co.crazy_bakery.infrastructure.web.client.ColombiaApiClient;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeografiaServiceImplTest {

    @Mock
    private ColombiaApiClient colombiaApiClient;

    @InjectMocks
    private GeografiaServiceImpl geografiaService;

    @Test
    void getDepartamentos_ShouldReturnListDeDepartamentos() {
        DepartamentoDTO departamento = new DepartamentoDTO();
        departamento.setId(1);
        departamento.setName("Amazonas");
        List<DepartamentoDTO> expected = Collections.singletonList(departamento);

        when(colombiaApiClient.getDepartamentos()).thenReturn(expected);

        List<DepartamentoDTO> result = geografiaService.getDepartamentos();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Amazonas");
        verify(colombiaApiClient).getDepartamentos();
    }

    @Test
    void getCiudades_ShouldReturnListDeCiudades() {
        CiudadDTO ciudad = new CiudadDTO();
        ciudad.setId(1);
        ciudad.setName("Leticia");
        List<CiudadDTO> expected = Collections.singletonList(ciudad);

        when(colombiaApiClient.getCiudades()).thenReturn(expected);

        List<CiudadDTO> result = geografiaService.getCiudades();

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Leticia");
        verify(colombiaApiClient).getCiudades();
    }
}
