
package uan.edu.co.crazy_bakery.domain.model;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uan.edu.co.crazy_bakery.domain.enums.TipoIngrediente;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

@Entity
@Table(name = "ingredientes_costos")
@Immutable
@IdClass(IngredienteCostoId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredienteCosto {

    @Column(name = "tipo_receta", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoReceta tipoReceta;

    @Id
    @Column(name = "tamano_id")
    private Long tamanoId;

    @Column(name = "tamano_nombre")
    private String tamanoNombre;

    @Column(name = "tamano_tiempo")
    private float tamanoTiempo;

    @Column(name = "tipo_ingrediente")
    @Enumerated(EnumType.STRING)
    private TipoIngrediente tipoIngrediente;

    @Id
    @Column(name = "ingrediente_id")
    private Long ingredienteId;

    @Column(name = "ingrediente_nombre")
    private String ingredienteNombre;

    @Column(name = "ingrediente_costo_total")
    private double ingredienteCostoTotal;
}
