package uan.edu.co.crazy_bakery.application.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Schema(description = "Nota interna asociada a una orden")
@Data
@Builder
public class NotaDTO {

    @Schema(description = "ID único de la nota", example = "1")
    private Long id;

    @Schema(description = "Fecha y hora en que se registró la nota", example = "2024-03-15T10:30:00")
    private Date fechaCreacion;

    @Schema(description = "Texto de la nota o instrucción especial", example = "El cliente solicita decoración con flores rosas")
    private String nota;

    @Schema(description = "Nombre completo del usuario que registró la nota", example = "María García")
    private String usuarioNombre;
}
