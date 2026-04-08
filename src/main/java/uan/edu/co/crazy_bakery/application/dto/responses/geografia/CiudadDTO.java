package uan.edu.co.crazy_bakery.application.dto.responses.geografia;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Representación de una ciudad de Colombia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CiudadDTO {

    @Schema(description = "ID único de la ciudad", example = "1")
    private int id;

    @Schema(description = "Nombre de la ciudad", example = "Bogotá")
    private String name;

    @Schema(description = "ID del departamento al que pertenece la ciudad", example = "25")
    private int departmentId;
}
