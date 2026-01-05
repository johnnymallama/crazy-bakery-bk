package uan.edu.co.crazy_bakery.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoUsuario;

@Data
@Entity(name = "usuario")
public class Usuario {
    @Id
    private String id;
    private String email;
    private String nombre;
    private String apellido;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipo;
    
    private String telefono;
    private String direccion;
    private String departamento;
    private String ciudad;
    private boolean estado;
}
