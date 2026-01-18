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
        @Mapping(target = "id", source = "ingrediente.id"),
        @Mapping(target = "nombre", source = "ingrediente.nombre"),
        @Mapping(target = "composicion", source = "ingrediente.composicion"),
        @Mapping(target = "tipoIngrediente", source = "ingrediente.tipoIngrediente"),
        @Mapping(target = "valor", source = "ingrediente.valor"),
        @Mapping(target = "estado", source = "ingrediente.estado")
    })
    IngredienteDTO ingredienteToIngredienteDTO(Ingrediente ingrediente);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Ingrediente crearIngredienteDTOToIngrediente(CrearIngredienteDTO crearIngredienteDTO);
}
