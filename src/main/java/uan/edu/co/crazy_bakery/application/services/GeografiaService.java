package uan.edu.co.crazy_bakery.application.services;

import uan.edu.co.crazy_bakery.application.dto.responses.geografia.CiudadDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.geografia.DepartamentoDTO;

import java.util.List;

public interface GeografiaService {
    List<DepartamentoDTO> getDepartamentos();
    List<CiudadDTO> getCiudades();
}
