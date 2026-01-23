package uan.edu.co.crazy_bakery.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uan.edu.co.crazy_bakery.application.dto.requests.CrearOrdenDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.OrdenDTO;
import uan.edu.co.crazy_bakery.domain.model.Orden;

@Mapper(componentModel = "spring", uses = {RecetaMapper.class})
public interface OrdenMapper {

    @Mapping(source = "usuario.id", target = "usuarioId")
    OrdenDTO toDto(Orden orden);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fecha", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "recetas", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "valorTotal", ignore = true)
    Orden toEntity(CrearOrdenDTO crearOrdenDTO);
}
