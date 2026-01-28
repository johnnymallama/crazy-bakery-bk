package uan.edu.co.crazy_bakery.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uan.edu.co.crazy_bakery.domain.enums.EstadoOrden;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orden")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fecha;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
            name = "orden_receta",
            joinColumns = @JoinColumn(name = "orden_id"),
            inverseJoinColumns = @JoinColumn(name = "receta_id")
    )
    private List<Receta> recetas;

    @ElementCollection
    @CollectionTable(name = "orden_notas", joinColumns = @JoinColumn(name = "orden_id"))
    @Column(name = "nota")
    private List<String> notas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrden estado;

    @Builder.Default
    @Column(name = "valor_total", nullable = false)
    private float valorTotal = 0f;

    @Builder.Default
    @Column(name = "ganancia", nullable = false)
    private float ganancia = 0f;

    public void setGanancia(float gananciaInput) {
        this.ganancia = this.ganancia + gananciaInput;
    }

    public void setValorTotal(float valorTotalInput) {
        this.valorTotal = this.valorTotal + valorTotalInput;
    }

}
