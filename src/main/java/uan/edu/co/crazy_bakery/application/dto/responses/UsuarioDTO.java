package uan.edu.co.crazy_bakery.application.dto.responses;

import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoUsuario;

@Data
public class UsuarioDTO {
    private String id;
    private String email;
    private String nombre;
    private String apellido;
    private TipoUsuario tipo;
    private String telefono;
    private String direccion;
    private String departamento;
    private String ciudad;
}
