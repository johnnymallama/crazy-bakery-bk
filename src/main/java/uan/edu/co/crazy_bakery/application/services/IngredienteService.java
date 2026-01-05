package uan.edu.co.crazy_bakery.application.services;

import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;

import java.util.List;

public interface IngredienteService {
    List<IngredienteDTO> getAllIngredientes();
    IngredienteDTO getIngredienteById(String id);
    IngredienteDTO createIngrediente(CrearIngredienteDTO crearIngredienteDTO);
    IngredienteDTO updateIngrediente(String id, CrearIngredienteDTO crearIngredienteDTO);
    void deleteIngrediente(String id);
}
