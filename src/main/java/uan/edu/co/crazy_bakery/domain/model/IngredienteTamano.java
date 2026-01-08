package uan.edu.co.crazy_bakery.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;

@Data
@Entity
@Table(name = "tamano_tipo_ingrediente")
public class IngredienteTamano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tamano_id", nullable = false)
    private Tamano tamano;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ingrediente", nullable = false)
    private TipoIngrediente tipoIngrediente;

    @Column(nullable = false)
    private float gramos;

    @Column(nullable = false)
    private boolean estado = true;
}
