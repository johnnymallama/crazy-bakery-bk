package uan.edu.co.crazy_bakery.application.services.impl;

import org.springframework.stereotype.Service;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.CiudadDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.DepartamentoDTO;
import uan.edu.co.crazy_bakery.application.services.GeografiaService;
import uan.edu.co.crazy_bakery.infrastructure.web.client.ColombiaApiClient;

import java.util.List;

@Service
public class GeografiaServiceImpl implements GeografiaService {

    private final ColombiaApiClient colombiaApiClient;

    public GeografiaServiceImpl(ColombiaApiClient colombiaApiClient) {
        this.colombiaApiClient = colombiaApiClient;
    }

    @Override
    public List<DepartamentoDTO> getDepartamentos() {
        return colombiaApiClient.getDepartamentos();
    }

    @Override
    public List<CiudadDTO> getCiudades() {
        return colombiaApiClient.getCiudades();
    }
}
