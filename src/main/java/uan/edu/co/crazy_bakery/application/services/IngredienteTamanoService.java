package uan.edu.co.crazy_bakery.application.services;

import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteTamanoDTO;

import java.util.List;

public interface IngredienteTamanoService {

    List<IngredienteTamanoDTO> consultarPorTamano(Long tamanoId);

    IngredienteTamanoDTO crearRelacion(CrearIngredienteTamanoDTO crearIngredienteTamanoDTO);

    boolean inactivarRelacion(Long id);
}
