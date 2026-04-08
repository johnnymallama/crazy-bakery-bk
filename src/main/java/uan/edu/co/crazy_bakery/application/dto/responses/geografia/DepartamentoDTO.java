package uan.edu.co.crazy_bakery.application.dto.responses.geografia;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Representación de un departamento de Colombia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartamentoDTO {

    @Schema(description = "ID único del departamento", example = "25")
    private int id;

    @Schema(description = "Nombre del departamento", example = "Cundinamarca")
    private String name;
}
