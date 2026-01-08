package uan.edu.co.crazy_bakery.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import uan.edu.co.crazy_bakery.application.dto.responses.IngredienteTamanoDTO;
import uan.edu.co.crazy_bakery.domain.model.IngredienteTamano;

@Mapper
public interface IngredienteTamanoMapper {

    IngredienteTamanoMapper INSTANCE = Mappers.getMapper(IngredienteTamanoMapper.class);

    @Mapping(source = "tamano.id", target = "tamanoId")
    @Mapping(source = "tamano.nombre", target = "tamanoNombre")
    IngredienteTamanoDTO ingredienteTamanoToIngredienteTamanoDTO(IngredienteTamano ingredienteTamano);
}
