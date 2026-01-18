package uan.edu.co.crazy_bakery.application.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import uan.edu.co.crazy_bakery.application.dto.requests.CrearTamanoDTO;
import uan.edu.co.crazy_bakery.application.dto.responses.TamanoDTO;
import uan.edu.co.crazy_bakery.domain.model.Tamano;

@Mapper(componentModel = "spring")
public interface TamanoMapper {

    TamanoMapper INSTANCE = Mappers.getMapper(TamanoMapper.class);

    TamanoDTO tamanoToTamanoDTO(Tamano tamano);

    List<TamanoDTO> tamanosToTamanoDTOs(List<Tamano> tamano);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", ignore = true)
    Tamano crearTamanoDTOToTamano(CrearTamanoDTO crearTamanoDTO);

}
