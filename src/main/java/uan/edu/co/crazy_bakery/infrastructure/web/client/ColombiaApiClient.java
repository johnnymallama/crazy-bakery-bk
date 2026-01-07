package uan.edu.co.crazy_bakery.infrastructure.web.client;

import uan.edu.co.crazy_bakery.application.dto.responses.geografia.CiudadDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.DepartamentoDTO;

import java.util.List;

public interface ColombiaApiClient {
    List<DepartamentoDTO> getDepartamentos();
    List<CiudadDTO> getCiudades();
}
