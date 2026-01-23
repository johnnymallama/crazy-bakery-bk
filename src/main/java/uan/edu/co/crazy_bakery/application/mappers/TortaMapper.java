package uan.edu.co.crazy_bakery.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uan.edu.co.crazy_bakery.domain.model.Torta;
import uan.edu.co.crazy_bakery.application.dto.torta.TortaDTO;

@Mapper(componentModel = "spring", uses = {TamanoMapper.class, IngredienteMapper.class})
public interface TortaMapper {

    TortaDTO toDTO(Torta torta);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "valor", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Torta toEntity(TortaDTO tortaDTO);
}
