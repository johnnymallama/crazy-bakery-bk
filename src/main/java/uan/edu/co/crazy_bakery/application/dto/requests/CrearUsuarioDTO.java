package uan.edu.co.crazy_bakery.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoUsuario;

@Schema(description = "Datos para registrar un nuevo usuario sincronizado con Firebase Authentication")
@Data
public class CrearUsuarioDTO {

    @Schema(description = "UID único del usuario generado por Firebase Authentication", example = "uid_firebase_abc123")
    @NotBlank
    private String id;

    @Schema(description = "Correo electrónico del usuario", example = "usuario@correo.com")
    @Email
    private String email;

    @Schema(description = "Nombre del usuario", example = "María")
    @NotBlank
    private String nombre;

    @Schema(description = "Apellido del usuario", example = "García")
    @NotBlank
    private String apellido;

    @Schema(description = "Rol del usuario en la plataforma (ADMIN, CLIENTE)", example = "CLIENTE")
    private TipoUsuario tipo;

    @Schema(description = "Número de teléfono de contacto", example = "3001234567")
    private String telefono;

    @Schema(description = "Dirección de domicilio", example = "Calle 45 #12-34")
    private String direccion;

    @Schema(description = "Nombre del departamento de residencia", example = "Cundinamarca")
    private String departamento;

    @Schema(description = "Nombre de la ciudad de residencia", example = "Bogotá")
    private String ciudad;
}
