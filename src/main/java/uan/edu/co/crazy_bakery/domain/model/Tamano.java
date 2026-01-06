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
@Table(name = "tamano")
public class Tamano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private int alto;

    @Column(nullable = false)
    private int diametro;

    @Column(nullable = false)
    private int porciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_receta", nullable = false)
    private TipoReceta tipoReceta;

    @Column(nullable = false)
    private boolean estado = true;
}
