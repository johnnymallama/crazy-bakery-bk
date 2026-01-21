package uan.edu.co.crazy_bakery.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uan.edu.co.crazy_bakery.domain.enums.TipoReceta;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "receta")
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_receta", nullable = false)
    private TipoReceta tipoReceta;

    @ManyToOne
    @JoinColumn(name = "torta_id", nullable = false)
    private Torta torta;

    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private float costoTotal;

    @Column(nullable = true)
    private String prompt;

    @Column(nullable = true)
    private String imagenUrl;

    @Column(nullable = false)
    private boolean estado = true;
}
