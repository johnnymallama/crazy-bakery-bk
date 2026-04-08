package uan.edu.co.crazy_bakery.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Datos de contacto y ubicación para actualizar el perfil del usuario")
@Data
public class ActualizarUsuarioDTO {

    @Schema(description = "Número de teléfono de contacto", example = "3001234567")
    private String telefono;

    @Schema(description = "Dirección de domicilio", example = "Calle 45 #12-34")
    private String direccion;

    @Schema(description = "Nombre del departamento de residencia", example = "Cundinamarca")
    private String departamento;

    @Schema(description = "Nombre de la ciudad de residencia", example = "Bogotá")
    private String ciudad;
}
