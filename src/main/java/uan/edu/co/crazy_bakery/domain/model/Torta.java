package uan.edu.co.crazy_bakery.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "torta")
public class Torta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bizcocho_id", nullable = false)
    private Ingrediente bizcocho;

    @ManyToOne
    @JoinColumn(name = "relleno_id", nullable = false)
    private Ingrediente relleno;

    @ManyToOne
    @JoinColumn(name = "cubertura_id", nullable = false)
    private Ingrediente cubertura;

    @ManyToOne
    @JoinColumn(name = "tamano_id", nullable = false)
    private Tamano tamano;

    @Column(nullable = false)
    private float valor;

    @Column(nullable = false)
    private boolean estado;

}
