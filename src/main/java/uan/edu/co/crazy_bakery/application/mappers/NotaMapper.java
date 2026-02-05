package uan.edu.co.crazy_bakery.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import uan.edu.co.crazy_bakery.application.dto.responses.NotaDTO;
import uan.edu.co.crazy_bakery.domain.model.Nota;

@Mapper(componentModel = "spring")
public interface NotaMapper {

    @Mappings({
            @Mapping(source = "usuario.nombre", target = "usuarioNombre") // Corregido de 'name' a 'nombre'
    })
    NotaDTO toDto(Nota nota);
}
