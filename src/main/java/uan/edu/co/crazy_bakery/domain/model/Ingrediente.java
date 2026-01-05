package uan.edu.co.crazy_bakery.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

@Data
@Entity
@Table(name = "ingrediente")
public class Ingrediente {

    @Id
    @Column(name = "codigo")
    private String codigo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "composicion")
    private String composicion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ingrediente")
    private TipoIngrediente tipoIngrediente;

    @Column(name = "valor")
    private float valor;
}
