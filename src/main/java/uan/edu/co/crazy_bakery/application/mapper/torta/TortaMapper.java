package uan.edu.co.crazy_bakery.application.mapper.torta;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uan.edu.co.crazy_bakery.domain.model.Torta;
import uan.edu.co.crazy_bakery.application.dto.torta.TortaDTO;

@Mapper(componentModel = "spring")
public interface TortaMapper {

    TortaDTO toDTO(Torta torta);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "valor", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "bizcocho", ignore = true)
    @Mapping(target = "relleno", ignore = true)
    @Mapping(target = "cubertura", ignore = true)
    @Mapping(target = "porcion", ignore = true)
    Torta toEntity(TortaDTO tortaDTO);
}
