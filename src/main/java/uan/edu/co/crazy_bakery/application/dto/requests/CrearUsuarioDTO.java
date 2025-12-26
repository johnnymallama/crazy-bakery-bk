package uan.edu.co.crazy_bakery.application.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoUsuario;

@Data
public class CrearUsuarioDTO {
    @NotBlank
    private String id;
    @Email
    private String email;
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    private TipoUsuario tipo;
    private String telefono;
    private String direccion;
    private String departamento;
    private String ciudad;
}
