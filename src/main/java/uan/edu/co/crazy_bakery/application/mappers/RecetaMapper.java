package uan.edu.co.crazy_bakery.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearRecetaDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.RecetaDTO;
import uan.edu.co.crazy_bakery.domain.model.Receta;
import uan.edu.co.crazy_bakery.domain.model.Torta;

@Mapper(componentModel = "spring", uses = TortaMapper.class)
public interface RecetaMapper {

    RecetaMapper INSTANCE = Mappers.getMapper(RecetaMapper.class);

    RecetaDTO recetaToRecetaDTO(Receta receta);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "torta", source = "torta")
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "valor", ignore = true) // El valor se calculará en el servicio
    Receta crearRecetaDTOToReceta(CrearRecetaDTO crearRecetaDTO, Torta torta);
}
