package uan.edu.co.crazy_bakery.infrastructure.web.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.CiudadDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.DepartamentoDTO;

import java.util.List;

@Component
public class ColombiaApiClientImpl implements ColombiaApiClient {

    private final RestClient restClient;

    public ColombiaApiClientImpl(RestClient colombiaApiRestClient) {
        this.restClient = colombiaApiRestClient;
    }

    @Override
    public List<DepartamentoDTO> getDepartamentos() {
        return restClient.get()
                .uri("/Department")
                .retrieve()
                .body(new ParameterizedTypeReference<List<DepartamentoDTO>>() {});
    }

    @Override
    public List<CiudadDTO> getCiudades() {
        return restClient.get()
                .uri("/City")
                .retrieve()
                .body(new ParameterizedTypeReference<List<CiudadDTO>>() {});
    }
}
