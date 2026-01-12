package uan.edu.co.crazy_bakery.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;

@Mapper(componentModel = "spring")
public interface IngredienteMapper {

    IngredienteDTO ingredienteToIngredienteDTO(Ingrediente ingrediente);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Ingrediente crearIngredienteDTOToIngrediente(CrearIngredienteDTO crearIngredienteDTO);
}
