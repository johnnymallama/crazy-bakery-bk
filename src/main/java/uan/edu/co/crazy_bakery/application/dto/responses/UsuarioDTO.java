package uan.edu.co.crazy_bakery.application.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoUsuario;

@Schema(description = "Representación de un usuario registrado en la plataforma")
@Data
public class UsuarioDTO {

    @Schema(description = "UID único del usuario generado por Firebase Authentication", example = "uid_firebase_abc123")
    private String id;

    @Schema(description = "Correo electrónico del usuario", example = "usuario@correo.com")
    private String email;

    @Schema(description = "Nombre del usuario", example = "María")
    private String nombre;

    @Schema(description = "Apellido del usuario", example = "García")
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

    @Schema(description = "Indica si el usuario está activo en la plataforma", example = "true")
    private boolean estado;
}
