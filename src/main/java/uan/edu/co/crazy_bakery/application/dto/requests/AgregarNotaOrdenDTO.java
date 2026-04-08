package uan.edu.co.crazy_bakery.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "Datos para agregar una nota interna a una orden")
@Data
public class AgregarNotaOrdenDTO {

    @Schema(description = "Texto de la nota o instrucción especial", example = "El cliente solicita decoración con flores rosas")
    @NotBlank(message = "La nota no puede estar vacía")
    private String nota;

    @Schema(description = "UID del usuario de Firebase que registra la nota", example = "uid_firebase_abc123")
    @NotBlank(message = "El id del usuario no puede estar vacío")
    private String usuarioId;
}
