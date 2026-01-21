package uan.edu.co.crazy_bakery.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearIngredienteDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteDTO;
import uan.edu.co.crazy_bakery.domain.model.Ingrediente;

@Mapper(componentModel = "spring")
public interface IngredienteMapper {

    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "nombre", source = "nombre"),
        @Mapping(target = "composicion", source = "composicion"),
        @Mapping(target = "tipoIngrediente", source = "tipoIngrediente"),
        @Mapping(target = "costoPorGramo", source = "costoPorGramo"),
        @Mapping(target = "estado", source = "estado")
    })
    IngredienteDTO ingredienteToIngredienteDTO(Ingrediente ingrediente);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Ingrediente crearIngredienteDTOToIngrediente(CrearIngredienteDTO crearIngredienteDTO);
}
