package uan.edu.co.crazy_bakery.application.services;

import uan.edu.co.crazy_bakery.application.dto.requests.CrearRecetaDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.RecetaDTO;

import java.util.List;

public interface RecetaService {

    RecetaDTO crearReceta(CrearRecetaDTO crearRecetaDTO);

    RecetaDTO obtenerRecetaPorId(Long id);

    List<String> getUltimasImagenes();

}
