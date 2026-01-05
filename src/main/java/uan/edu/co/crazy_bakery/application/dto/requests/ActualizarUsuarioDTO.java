package uan.edu.co.crazy_bakery.application.dto.requests;

import lombok.Data;

@Data
public class ActualizarUsuarioDTO {
    private String telefono;
    private String direccion;
    private String departamento;
    private String ciudad;
}
