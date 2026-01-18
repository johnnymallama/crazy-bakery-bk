package uan.edu.co.crazy_bakery.application.services;

import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;

import java.util.List;
import java.util.Optional;

public interface IngredienteService {
    IngredienteDTO createIngrediente(CrearIngredienteDTO crearIngredienteDTO);

    Optional<IngredienteDTO> getIngrediente(Long id);

    List<IngredienteDTO> getAllIngredientes();

    Optional<IngredienteDTO> updateIngrediente(Long id, CrearIngredienteDTO crearIngredienteDTO);

    Optional<IngredienteDTO> deactivateIngrediente(Long id);

    List<IngredienteDTO> findByTipoIngrediente(TipoIngrediente tipoIngrediente);
}
