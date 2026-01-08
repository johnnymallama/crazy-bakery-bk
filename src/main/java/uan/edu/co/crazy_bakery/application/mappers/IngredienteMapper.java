package uan.edu.co.crazy_bakery.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;

@Mapper(componentModel = "spring")
public interface IngredienteMapper {

    IngredienteMapper INSTANCE = Mappers.getMapper(IngredienteMapper.class);

    IngredienteDTO ingredienteToIngredienteDTO(Ingrediente ingrediente);

    @Mapping(target = "id", ignore = true)
    Ingrediente crearIngredienteDTOToIngrediente(CrearIngredienteDTO crearIngredienteDTO);
}
