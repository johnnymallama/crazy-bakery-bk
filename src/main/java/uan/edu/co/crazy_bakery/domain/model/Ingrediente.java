package uan.edu.co.crazy_bakery.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

@Data
@Entity
@Table(name = "ingrediente")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "composicion")
    private String composicion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ingrediente")
    private TipoIngrediente tipoIngrediente;

    @Column(name = "costo_por_gramo")
    private float costoPorGramo;

    @Column(name = "estado")
    private Boolean estado = true;
}
