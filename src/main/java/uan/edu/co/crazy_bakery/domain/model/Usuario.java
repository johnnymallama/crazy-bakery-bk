package uan.edu.co.crazy_bakery.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uan.edu.co.crazy_bakery.domain.enums.TipoUsuario;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    
    @Column(nullable = false)
    private boolean estado = true;
}
