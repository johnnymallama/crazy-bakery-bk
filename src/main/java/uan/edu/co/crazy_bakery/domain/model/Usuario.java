package uan.edu.co.crazy_bakery.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "usuarios")
public class Usuario {
    @Id
    private String id;
    private String email;
    private String nombre;
    private String apellido;
    private String tipo;
    private String telefono;
    private String direccion;
    private String departamento;
    private String ciudad;
}
