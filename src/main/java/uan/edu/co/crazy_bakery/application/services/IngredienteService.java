package uan.edu.co.crazy_bakery.application.services;

import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;

import java.util.List;
import java.util.Optional;

public interface IngredienteService {

    IngredienteDTO createIngrediente(CrearIngredienteDTO crearIngredienteDTO);

    Optional<IngredienteDTO> getIngrediente(String id);

    List<IngredienteDTO> getAllIngredientes();
}
